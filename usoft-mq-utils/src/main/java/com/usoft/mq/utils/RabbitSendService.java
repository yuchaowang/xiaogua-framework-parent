package com.usoft.mq.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * RabbitMQ 发送消息
 *
 * @author: wangcanyi
 * @date: 2018-08-30 15:11
 **/
@Service
public class RabbitSendService implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitSendService.class);
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@PostConstruct
	public void init() {
//		如果消息没有到exchange,则confirm回调,ack=false
//		如果消息到达exchange,则confirm回调,ack=true
//		exchange到queue成功,则不回调return
//		exchange到queue失败,则回调return(否则不回回调,消息就丢了)
		rabbitTemplate.setConfirmCallback(this);
		rabbitTemplate.setReturnCallback(this);
	}

	/**
	 * 发送消息
	 *
	 * @param queueName 队列名
	 * @param userId    用户ID
	 * @param appId     应用号
	 * @param bizType   业务类型
	 * @param bizId     业务ID
	 * @return 消息ID
	 */
	public String sendMessage(String queueName, String userId, String appId, String bizType, String bizId) {
		return sendMessage(queueName, userId, appId, bizType, bizId, "");
	}

	/**
	 * 发送消息
	 *
	 * @param queueName  队列名
	 * @param userId     用户ID
	 * @param appId      应用号
	 * @param bizType    业务类型
	 * @param bizId      业务ID
	 * @param bizContent 业务内容
	 * @return 消息ID
	 */
	public String sendMessage(String queueName, String userId, String appId, String bizType, String bizId, String bizContent) {
		MessageInfo messageInfo = new MessageInfo();
		messageInfo.setUserId(userId);
		messageInfo.setAppId(appId);
		messageInfo.setBizType(bizType);
		messageInfo.setBizId(bizId);
		messageInfo.setBizContent(bizContent);
		return sendMessage(queueName, messageInfo);
	}


	/**
	 * 发送消息
	 *
	 * @param queueName   队列名
	 * @param messageInfo 消息体
	 * @return 消息ID
	 */
	public String sendMessage(String queueName, MessageInfo messageInfo) {
		if (StringUtils.isBlank(queueName)) {
			throw new IllegalArgumentException("queueName不能为空");
		}
		if (messageInfo == null) {
			throw new IllegalArgumentException("messageInfo不能为空");
		}
		//设置消息ID
		messageInfo.setMsgId(UUID.randomUUID().toString());
		//设置时间戳
		messageInfo.setTimestamp(System.currentTimeMillis());
		CorrelationData correlationData = new CorrelationData(messageInfo.getMsgId());
		String messageJson = JSON.toJSONString(messageInfo);
		rabbitTemplate.convertAndSend(queueName, (Object) messageJson, correlationData);
		LOGGER.info("发送消息[RabbitSendService.sendMessage].正常,queueName:{},messageInfo:{},correlationData:{}",
				queueName, messageJson, correlationData.getId());
		return messageInfo.getMsgId();
	}

	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		if (ack) {
			LOGGER.info("发送消息确认[RabbitSendService.confirm].正常,correlationData:{}", correlationData.getId());
		} else {
			LOGGER.error("发送消息确认[RabbitSendService.confirm].异常,correlationData:{},cause:{}", correlationData.getId(), cause);
		}
	}

	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
		LOGGER.error("发送消息确认到达队列[RabbitSendService.returnedMessage].异常,message:{},replyCode:{},replyText:{},exchange:{},routingKey:{}",
				message, replyCode, replyText, exchange, routingKey);
	}
}
