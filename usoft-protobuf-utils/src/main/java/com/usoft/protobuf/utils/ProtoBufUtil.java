package com.usoft.protobuf.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ProtoBuf 工具类
 *
 * @author wangcanyi
 * @date 2018-07-31 17:57
 */
public class ProtoBufUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProtoBufUtil.class);
	/**
	 * 日期格式
	 */
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/**
	 * ProtoBuf to Json Format
	 */
	private static final JsonFormat.Printer JSON_FORMAT_PRINTER = JsonFormat.printer().includingDefaultValueFields();
	/**
	 * Parser Json To ProtoBuf
	 */
	private static final JsonFormat.Parser JSON_FORMAT_PARSER = JsonFormat.parser().ignoringUnknownFields();

	/**
	 * ProtoBuf实体 转换为 Javabean
	 *
	 * @param message ProtoBuf实体
	 * @param cls     Javabean Class
	 * @param <T>
	 * @return Javabean
	 * @throws InvalidProtocolBufferException
	 */
	public static <T> T toBean(MessageOrBuilder message, Class<T> cls) throws InvalidProtocolBufferException {
		String jsonStr = JSON_FORMAT_PRINTER.print(message);
		return JSON.parseObject(jsonStr, cls);
	}

	/**
	 * JavaBean 转换为 ProtoBuf实体
	 *
	 * @param builder    ProtoBuf Builder
	 * @param beanSource JavaBean
	 * @param <T>
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	public static <T extends Message.Builder> T toProtoBuf(T builder, Object beanSource) throws InvalidProtocolBufferException {
		JSON.DEFFAULT_DATE_FORMAT = DATE_FORMAT;
		String jsonStr = JSON.toJSONString(beanSource, SerializerFeature.WriteDateUseDateFormat);
		JSON_FORMAT_PARSER.merge(jsonStr, builder);
		return builder;
	}

	/**
	 * ProtoBuf实体之间的转换
	 *
	 * @param builder        目标ProtoBuf实体
	 * @param protoBufSource 原来的ProtoBuf实体
	 * @param <T>
	 * @return
	 * @throws InvalidProtocolBufferException
	 */
	public static <T extends Message.Builder> T toProtoBuf(T builder, MessageOrBuilder protoBufSource) throws InvalidProtocolBufferException {
		JSON_FORMAT_PARSER.merge(JSON_FORMAT_PRINTER.print(protoBufSource), builder);
		return builder;
	}

	/**
	 * Json字符串 转 ProtoBuf实体
	 *
	 * @param builder    目标ProtoBuf实体
	 * @param jsonSource Json字符串
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	public static <T extends Message.Builder> T toProtoBuf(T builder, String jsonSource) throws InvalidProtocolBufferException {
		JSON_FORMAT_PARSER.merge(jsonSource, builder);
		return builder;
	}

	/**
	 * ProtoBuf实体 转 Json字符串
	 *
	 * @param message ProtoBuf实体
	 * @return Json字符串
	 * @throws InvalidProtocolBufferException
	 */
	public static String toJSON(MessageOrBuilder message) throws InvalidProtocolBufferException {
		return JSON_FORMAT_PRINTER.print(message);
	}

	/**
	 * ProtoBuf实体 转 Json字符串 捕获异常
	 *
	 * @param message
	 * @return
	 */
	public static String toJSONHasTryCatch(MessageOrBuilder message) {
		String jsonStr = "";
		try {
			jsonStr = JSON_FORMAT_PRINTER.print(message);
		} catch (InvalidProtocolBufferException e) {
			LOGGER.error("ProtoBuf实体 转 Json字符串 捕获异常[ProtoBufUtil.toJSONHasTryCatch]异常，参数：{}", message, e);
		}
		return jsonStr;
	}
}
