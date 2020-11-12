package com.usoft.bizcode.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

/**
 * @author: wangcanyi
 * @date: 2018-08-23 17:18
 **/
public class BizCodeUtilTest {
	@Test
	public void genBizCode(){
		System.out.println(System.currentTimeMillis() + ":" + BizCodeUtil.genBizCode("XX"));
		for(int i = 10; i < 100;i++){
			System.out.println(System.currentTimeMillis() + ":" + BizCodeUtil.genLongBizCode("" + i ));
		}
		System.out.println(System.currentTimeMillis() + ":" + BizCodeUtil.genLongBizCode("XX"));

	}
}
