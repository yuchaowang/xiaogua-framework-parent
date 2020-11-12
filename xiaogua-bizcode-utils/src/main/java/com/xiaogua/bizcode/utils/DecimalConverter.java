package com.xiaogua.bizcode.utils;

import java.math.BigInteger;

/**
 * 十进制(Decimal)转换为 2-62进制
 *
 * @author: wangyc
 * @date: 2020-11-12
 */
public class DecimalConverter {

    private static final String[] NUM_TABLE =
        {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
            "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private static final int MAX_RADIX = 62;
    private static final int MIN_RADIX = 2;

    /**
     * 十进制(Decimal)转换为 2-62进制
     *
     * @param decimalText 10进制数
     * @param radix       几进制
     * @return
     */
    public static String fromDecimal(String decimalText, int radix) {
        if (decimalText == null || decimalText.trim().length() < 1) {
            return decimalText;
        }
        BigInteger radixInteger = new BigInteger(Integer.toString(radix));
        BigInteger decimalInteger = new BigInteger(decimalText);
        return fromDecimal(decimalInteger, radixInteger);
    }

    /**
     * 十进制(Decimal)转换为 2-62进制
     *
     * @param decimalValue 10进制数
     * @param radixInteger 几进制
     * @return
     */
    public static String fromDecimal(BigInteger decimalValue, BigInteger radixInteger) {

        if (radixInteger.intValue() < MIN_RADIX || radixInteger.intValue() > MAX_RADIX) {
            throw new IllegalArgumentException("只能转换为2-62进制");
        }

        if (decimalValue.compareTo(BigInteger.ZERO) < 0) {
            return "-" + fromDecimal(decimalValue.negate(), radixInteger);
        }

        if (decimalValue.compareTo(radixInteger) < 0) {
            return NUM_TABLE[decimalValue.intValue()];
        } else {
            BigInteger[] result = decimalValue.divideAndRemainder(radixInteger);
            // 整除的余数
            BigInteger suffix = result[1];
            String prefix = fromDecimal(result[0], radixInteger);
            return prefix + NUM_TABLE[suffix.intValue()];
        }
    }
}
