package com.xiaogua.bizcode.utils;

import org.junit.jupiter.api.Test;

/**
 * @author: wangyc
 * @date: 2020-11-12
 **/
public class BizCodeUtilTest {
    @Test
    public void genBizCode() {
        System.out.println(System.currentTimeMillis() + ":" + BizCodeUtil.genBizCode("XX"));
        for (int i = 10; i < 100; i++) {
            System.out.println(System.currentTimeMillis() + ":" + BizCodeUtil.genLongBizCode("" + i));
        }
        System.out.println(System.currentTimeMillis() + ":" + BizCodeUtil.genLongBizCode("XX"));

    }
}
