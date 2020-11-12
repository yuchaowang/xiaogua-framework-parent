package com.usoft.bizcode.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 业务号 工具类
 *
 * @author: wangcanyi
 * @date: 2018-08-23 16:51
 **/
public class LongCodeUtil {
	private static final int CODE_MAX_LENGTH = 20;
	private static final int PREFIX_LENGTH = 2;
	private static final int SE_MAX = 99;
	private static final int SE_LENGTH = 2;
	private static int se = 0;


	/**
	 * 创建业务号  最长20位
	 *
	 * @param prefix 前缀 - 自定义 - 长度：2位
	 * @return
	 */
	public static String genBizCode(String prefix) {
		if (StringUtils.isBlank(prefix)) {
			throw new IllegalArgumentException("prefix不能为空");
		}
		if (prefix.length() != PREFIX_LENGTH) {
			throw new IllegalArgumentException("prefix长度必须为2位");
		}
		String bizCode;
		//当前精确到0.1毫秒
		String code = genBizCode();
		bizCode = prefix + code.substring(2);
		if (bizCode.length() > CODE_MAX_LENGTH) {
			throw new RuntimeException("生成业务号长度错误[" + bizCode + "]");
		}
		return bizCode;
	}

	/**
	 * 创建业务号
	 *
	 * @return
	 */
	private static String genBizCode() {
		String bizCode = "";
		synchronized (LongCodeUtil.class) {
			if (se > SE_MAX) {
				se = 0;
			}
			//当前精确到0.1毫秒
			String currentDate = DateUtil.getCurrentDate(DateUtil.PATTERN_SIMPLE_DATE);
			String timestamp = "" + System.currentTimeMillis();
			bizCode = currentDate + timestamp.substring(3, 13) + BizCodeUtil.createSerial("" + se, SE_LENGTH);
			se++;
		}
		return bizCode;
	}
}
