package com.usoft.math.utils;

/**
 * @author: wangcanyi
 * @date: 2019-06-25 11:48
 **/
public class IntegerUtil {

	/**
	 * 判断是否为Integer
	 *
	 * @param s
	 * @return
	 */
	public static boolean isInteger(String s) {
		return parseInt(s) != null ? true : false;
	}

	/**
	 * 将字符串转换为Integer值
	 *
	 * @param s
	 * @return 无法转换时，返回null
	 */
	public static Integer parseInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
