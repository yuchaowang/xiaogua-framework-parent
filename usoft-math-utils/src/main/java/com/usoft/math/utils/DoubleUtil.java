package com.usoft.math.utils;

import java.math.BigDecimal;

/**
 * Double 计算
 *
 * @author wangcanyi
 */
public class DoubleUtil {
	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
	 * 定精度，以后的数字四舍五入。
	 *
	 * @param v1    被除数
	 * @param v2    除数
	 * @param scale 表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		return divBigDecimal(v1, v2, scale).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度
	 *
	 * @param v1           被除数
	 * @param v2           除数
	 * @param scale        表示表示需要精确到小数点以后几位
	 * @param roundingMode 舍入模式(例：BigDecimal.ROUND_UP)
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale, int roundingMode) {
		return divBigDecimal(v1, v2, scale, roundingMode).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
	 * 定精度，以后的数字四舍五入。
	 *
	 * @param v1    被除数
	 * @param v2    除数
	 * @param scale 表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static BigDecimal divBigDecimal(double v1, double v2, int scale) {
		return divBigDecimal(v1, v2, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度
	 *
	 * @param v1           被除数
	 * @param v2           除数
	 * @param scale        表示需要精确到小数点以后几位。
	 * @param roundingMode 舍入模式(例：BigDecimal.ROUND_UP)
	 * @return 两个参数的商
	 */
	public static BigDecimal divBigDecimal(double v1, double v2, int scale, int roundingMode) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, roundingMode);
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 *
	 * @param v     需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的结果
	 */

	public static double round(double v, int scale) {
		return round(v, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 提供精确的小数位处理
	 *
	 * @param v            需要处理的数字
	 * @param scale        小数点后保留几位
	 * @param roundingMode 舍入模式(例：BigDecimal.ROUND_UP)
	 * @return 处理后的结果
	 */
	public static double round(double v, int scale, int roundingMode) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = BigDecimal.ONE;
		return b.divide(one, scale, roundingMode).doubleValue();
	}

	/**
	 * 提供乘法运算处理。
	 *
	 * @param d1 被乘数
	 * @param d2 乘数
	 * @return 积
	 */
	public static double mul(double d1, double d2) { // 进行乘法运算
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供乘法运算处理。
	 *
	 * @param d1    被乘数
	 * @param d2    乘数
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的积
	 */
	public static double mul(double d1, double d2, int scale) { // 进行乘法运算
		return round(mul(d1, d2), scale);
	}

	/**
	 * 提供乘法运算处理。
	 *
	 * @param d1           被乘数
	 * @param d2           乘数
	 * @param scale        小数点后保留几位
	 * @param roundingMode 舍入模式(例：BigDecimal.ROUND_UP)
	 * @return 积
	 */
	public static double mul(double d1, double d2, int scale, int roundingMode) { // 进行乘法运算
		return round(mul(d1, d2), scale, roundingMode);
	}

	/**
	 * 提供加法运算处理。
	 *
	 * @param b1 被加数
	 * @param b2 加数
	 * @return 和
	 */
	public static double plus(double b1, double b2) {
		return new BigDecimal(Double.toString(b1)).add(new BigDecimal(Double.toString(b2))).doubleValue();
	}

	/**
	 * 提供加法运算处理。 并进行小数位四舍五入处理。
	 *
	 * @param b1    被加数
	 * @param b2    加数
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的和
	 */
	public static double plus(double b1, double b2, int scale) {
		return round(plus(b1, b2), scale);
	}

	/**
	 * 提供加法运算处理。 并进行小数位处理。
	 *
	 * @param b1           被加数
	 * @param b2           加数
	 * @param scale        小数点后保留几位
	 * @param roundingMode 舍入模式(例：BigDecimal.ROUND_UP)
	 * @return 和
	 */
	public static double plus(double b1, double b2, int scale, int roundingMode) {
		return round(plus(b1, b2), scale, roundingMode);
	}

	/**
	 * 提供减法运算处理。
	 *
	 * @param b1 被减数
	 * @param b2 减数
	 * @return 差
	 */
	public static double subtract(double b1, double b2) {
		return new BigDecimal(Double.toString(b1)).subtract(new BigDecimal(Double.toString(b2))).doubleValue();
	}

	/**
	 * 提供减法运算处理。并进行小数位四舍五入处理。
	 *
	 * @param b1    被减数
	 * @param b2    减数
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的差
	 */
	public static double subtract(double b1, double b2, int scale) {
		return round(subtract(b1, b2), scale);
	}

	/**
	 * 提供减法运算处理。并进行小数位处理。
	 *
	 * @param b1           被减数
	 * @param b2           减数
	 * @param scale        小数点后保留几位
	 * @param roundingMode 舍入模式(例：BigDecimal.ROUND_UP)
	 * @return 差
	 */
	public static double subtract(double b1, double b2, int scale, int roundingMode) {
		return round(subtract(b1, b2), scale, roundingMode);
	}
}
