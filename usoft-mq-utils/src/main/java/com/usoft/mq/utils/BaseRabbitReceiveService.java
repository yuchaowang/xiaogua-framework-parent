package com.usoft.mq.utils;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationConfigurationException;
import org.springframework.messaging.handler.annotation.Headers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMq 消息接收处理基类
 *
 * @author: wangcanyi
 * @date: 2018-08-30 17:41
 **/
public abstract class BaseRabbitReceiveService implements ChannelAwareMessageListener, InitializingBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseRabbitReceiveService.class);
	private static final String DELAY_QUEUE_NAME_SUFFIX = "_delay";
	/**
	 * 是否动态创建消息监听
	 */
	protected boolean isDynamicCreate = false;
	/**
	 * IP地址
	 */
	protected String host;
	/**
	 * 端口
	 */
	protected int port;
	/**
	 * Virtual Host
	 */
	protected String virtualHost;
	/**
	 * 用户名
	 */
	protected String username;
	/**
	 * 密码
	 */
	protected String password;
	/**
	 * 队列名
	 */
	protected String queueName;
	/**
	 * 延时时间，单位：毫秒
	 */
	protected String delayTime = "60000";
	/**
	 * 心跳时间，单位秒
	 */
	protected int requestedHeartBeat = 30;
	/**
	 * 延时队列名
	 */
	private String delayQueueName;

	/**
	 * 初始化连接工厂
	 *
	 * @return
	 */
	private ConnectionFactory initConnectionFactory() {
		if (StringUtils.isBlank(host)) {
			throw new IllegalArgumentException("host为空");
		}
		if (port <= 0) {
			throw new IllegalArgumentException("port小于等于0");
		}
		if (StringUtils.isBlank(virtualHost)) {
			throw new IllegalArgumentException("virtualHost为空");
		}
		if (StringUtils.isBlank(username)) {
			throw new IllegalArgumentException("username为空");
		}
		if (StringUtils.isBlank(password)) {
			throw new IllegalArgumentException("password为空");
		}
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setHost(host);
		connectionFactory.setPort(port);
		connectionFactory.setVirtualHost(virtualHost);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		connectionFactory.setRequestedHeartBeat(requestedHeartBeat);
		return connectionFactory;
	}

	/**
	 * 动态创建消息监听
	 *
	 * @return
	 */
	private SimpleMessageListenerContainer simpleMessageListenerContainer() {
		if (!isDynamicCreate) {
			return null;
		}
		if (this.getClass().isAnnotationPresent(RabbitListener.class)) {
			throw new AnnotationConfigurationException("动态创建时，不能配置@RabbitListener注解");
		}
		if (StringUtils.isBlank(queueName)) {
			throw new IllegalArgumentException("queueName为空");
		}
		ConnectionFactory connectionFactory = initConnectionFactory();
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
		container.setQueueNames(queueName);
		//设置手动应答
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		//使用ChannelAwareMessageListener接口，必须实现OnMessage方法
		container.setMessageListener(this);
		return container;
	}

	/**
	 * 实现InitializingBean的afterPropertiesSet方法，是为了在子类PostConstruct后，才执行该方法
	 */
	@Override
	public void afterPropertiesSet() {
		SimpleMessageListenerContainer container = simpleMessageListenerContainer();
		if (container != null) {
			container.start();
		}
	}

	@Override
	public void onMessage(Message message, Channel channel) {
		String messageJson = new String(message.getBody());
		long tag = message.getMessageProperties().getDeliveryTag();
		String queueName = message.getMessageProperties().getConsumerQueue();
		processMessage(messageJson, channel, tag, queueName);
	}

	/**
	 * 接收消息处理
	 *
	 * @param messageBytes
	 * @param channel
	 * @param headers
	 */
	@RabbitHandler
	public void receiveMessage(byte[] messageBytes, Channel channel, @Headers Map<String, Object> headers) {
		receiveMessage(new String(messageBytes), channel, headers);
	}

	/**
	 * 接收消息处理
	 *
	 * @param messageJson
	 * @param channel
	 * @param headers
	 */
	@RabbitHandler
	public void receiveMessage(String messageJson, Channel channel, @Headers Map<String, Object> headers) {
		long tag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
		String queueName = (String) headers.get(AmqpHeaders.CONSUMER_QUEUE);
		processMessage(messageJson, channel, tag, queueName);
	}

	/**
	 * 处理消息
	 *
	 * @param messageJson 消息Json内容
	 * @param channel     管道
	 * @param tag         Delivery Tag
	 * @param queueName   队列名
	 */
	private void processMessage(String messageJson, Channel channel, long tag, String queueName) {
		MessageInfo messageInfo = getMessageInfo(messageJson);
		//当消息不符合格式MessageInfo格式时，丢弃消息
		if (messageInfo == null) {
			LOGGER.warn("接收消息处理.消息格式.异常,messageJson:{},tag:{}", messageJson, tag);
			basicNack(channel, tag);
			return;
		}
		try {
			LOGGER.info("接收消息处理[BaseRabbitReceiveService.receiveMessage].处理开始,messageJson:{},tag:{}", messageJson, tag);
			processMessage(messageInfo);
			LOGGER.info("接收消息处理[BaseRabbitReceiveService.receiveMessage].处理结束,messageJson:{},tag:{}", messageJson, tag);
		} catch (Exception e) {
			LOGGER.error("接收消息处理[BaseRabbitReceiveService.receiveMessage].异常,messageJson:{},tag:{}", messageJson, tag, e);
			//出现异常时，消息转发为延时消息
			sendDelayMessage(messageInfo, channel, queueName);
		} finally {
			basicAck(channel, tag);
		}
	}

	/**
	 * 获取消息内容实体
	 *
	 * @param messageJson
	 * @return
	 */
	private MessageInfo getMessageInfo(String messageJson) {
		MessageInfo messageInfo = null;
		try {
			messageInfo = JSON.parseObject(messageJson, MessageInfo.class);
		} catch (Exception e) {
			LOGGER.error("获取消息内容实体[BaseRabbitReceiveService.getMessageInfo].异常,messageJson:{}", messageJson, e);

		}
		return messageInfo;
	}

	/**
	 * 发送延时消息
	 *
	 * @param messageInfo
	 * @param channel
	 * @param queueName
	 */
	private void sendDelayMessage(MessageInfo messageInfo, Channel channel, String queueName) {
		//重试次数+1
		messageInfo.setRetryCount(messageInfo.getRetryCount() + 1);
		String messageJson = JSON.toJSONString(messageInfo);
		try {
			String dQueueName = getDelayQueueName(channel, queueName);
			AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
			//设置延时时间
			builder.expiration(delayTime);
			channel.basicPublish("", dQueueName, builder.build(), messageJson.getBytes());
			LOGGER.info("发送延时消息[BaseRabbitReceiveService.sendDelayMessage].正常,messageJson:{},queueName:{}", messageJson, queueName);
		} catch (IOException e) {
			LOGGER.error("发送延时消息[BaseRabbitReceiveService.sendDelayMessage].异常,messageJson:{},queueName:{}", messageJson, queueName, e);
		}
	}

	/**
	 * 获取延时队列名
	 *
	 * @param channel
	 * @param queueName
	 * @return
	 * @throws IOException
	 */
	private String getDelayQueueName(Channel channel, String queueName) throws IOException {
		if (StringUtils.isNotBlank(delayQueueName)) {
			return delayQueueName;
		}
		//初始化延时队列
		String dQueueName = queueName + DELAY_QUEUE_NAME_SUFFIX;
		Map<String, Object> arguments = new HashMap<>(2);
		arguments.put("x-dead-letter-exchange", "");
		arguments.put("x-dead-letter-routing-key", queueName);
		channel.queueDeclare(dQueueName, true, false, false, arguments);
		delayQueueName = dQueueName;
		return dQueueName;
	}

	/**
	 * 消息应答No
	 *
	 * @param channel
	 * @param tag
	 */
	private void basicNack(Channel channel, long tag) {
		try {
			channel.basicNack(tag, false, false);
			LOGGER.info("接收消息处理.消息应答No[BaseRabbitReceiveService.receiveMessage.basicNack].正常,tag:{}", tag);
		} catch (IOException e) {
			LOGGER.error("接收消息处理.消息应答No[BaseRabbitReceiveService.receiveMessage.basicNack].异常,tag:{}", tag, e);
		}
	}

	/**
	 * 消息应答Yes
	 *
	 * @param channel
	 * @param tag
	 */
	private void basicAck(Channel channel, long tag) {
		try {
			channel.basicAck(tag, false);
			LOGGER.info("接收消息处理.消息应答Yes[BaseRabbitReceiveService.receiveMessage.basicAck].正常,tag:{}", tag);
		} catch (IOException e) {
			LOGGER.error("接收消息处理.消息应答Yes[BaseRabbitReceiveService.receiveMessage.basicAck].异常,tag:{}", tag, e);
		}
	}

	/**
	 * 处理消息
	 *
	 * @param messageInfo 消息体
	 * @throws Exception 处理失败时，抛出异常
	 */
	public abstract void processMessage(MessageInfo messageInfo) throws Exception;
}
