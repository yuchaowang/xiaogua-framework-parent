package com.xiaogua.bizcode.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 *
 * @author: wangyc
 * @date: 2020-11-12
 */
public class DateUtil {

    public static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_SIMPLE_DATE = "yyyyMMdd";

    private DateUtil() {
    }

    /***
     * 获取当前日期
     * @param pattern 格式：yyyyMMdd etc.
     * @return
     */
    public static String getCurrentDate(String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.SIMPLIFIED_CHINESE);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        return dateTimeFormatter.format(localDateTime);
    }

    /**
     * 字符串转为日期
     *
     * @param str
     * @param pattern
     * @return 年月日
     */
    public static Date toDate(String str, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.SIMPLIFIED_CHINESE);
        LocalDateTime localDateTime = LocalDateTime.parse(str, dateTimeFormatter);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * 日期转字符串
     *
     * @param date
     * @param pattern
     * @return 年月日
     */
    public static String toString(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.SIMPLIFIED_CHINESE);
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTimeFormatter.format(localDateTime);
    }
}
