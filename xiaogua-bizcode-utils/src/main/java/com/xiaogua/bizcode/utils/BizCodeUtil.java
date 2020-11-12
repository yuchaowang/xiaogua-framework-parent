package com.xiaogua.bizcode.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 业务号 工具类
 *
 * @author: wangyc
 * @date: 2020-11-12
 **/
public class BizCodeUtil {
    private static final int BIZ_CODE_MAX_LENGTH = 10;
    private static final int PREFIX_LENGTH = 2;
    private static final int SE_MAX = 9;
    private static final int SE_LENGTH = 1;
    private static int se = 0;

    /**
     * 创建业务号  最长10位
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
        String nextCode;
        //当前精确到0.1毫秒
        String decimalCode = genBizCode();
        //10进制转62进制
        nextCode = prefix + DecimalConverter.fromDecimal(decimalCode, 62);
        if (nextCode.length() > BIZ_CODE_MAX_LENGTH) {
            throw new RuntimeException("生成业务号长度错误[" + nextCode + "]");
        }
        return nextCode;
    }

    /**
     * 创建长业务号
     *
     * @param prefix
     * @return
     */
    public static String genLongBizCode(String prefix) {
        return LongCodeUtil.genBizCode(prefix);
    }

    /**
     * 创建业务号
     *
     * @return
     */
    private static String genBizCode() {
        String bizCode = "";
        synchronized (BizCodeUtil.class) {
            if (se > SE_MAX) {
                se = 0;
            }
            //当前精确到0.1毫秒
            bizCode = "" + System.currentTimeMillis() + createSerial("" + se, SE_LENGTH);
            se++;
        }
        return bizCode;
    }

    /**
     * 生成固定长度的序列号，不足位数在前补0
     *
     * @param src
     * @param len
     * @return
     */
    public static String createSerial(String src, int len) {
        String dest = "";
        if (src.length() >= len) {
            dest = src.substring(0, len);
        } else {
            dest = createSameChar("0", len - src.length()) + src;
        }
        return dest;
    }

    /**
     * 返回相同字符的串
     */
    private static String createSameChar(String src, int len) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb.append(src);
        }
        return sb.toString();
    }
}
