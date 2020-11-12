package com.xiaogua.bizcode.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author: wangyc
 * @date: 2020-11-12
 **/
public class DecimalConverterTest {
    @Test
    public void fromDecimal() throws IOException {
        long l = System.currentTimeMillis();
        System.out.println(l);
        System.out.println(DecimalConverter.fromDecimal("" + l + "9", 62));
    }

}
