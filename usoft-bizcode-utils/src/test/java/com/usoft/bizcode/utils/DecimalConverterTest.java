package com.usoft.bizcode.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author: wangcanyi
 * @date: 2018-08-23 15:03
 **/
public class DecimalConverterTest {
	@Test
	public void fromDecimal() throws IOException {
		long l = System.currentTimeMillis();
		System.out.println(l);
		System.out.println(DecimalConverter.fromDecimal("" + l + "9", 62));
	}

}
