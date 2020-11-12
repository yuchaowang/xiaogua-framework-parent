package com.xiaogua.math.utils;

import org.junit.jupiter.api.Test;

/**
 * Double 计算单元测试
 *
 * @author wangyc
 * @Date 2020-11-12
 */
public class DoubleUtilTest {
    @Test
    public void div() {
        System.out.println(DoubleUtil.div(2.0, 3.0, 2));
    }

    @Test
    public void mul() {
        System.out.println(DoubleUtil.mul(1.000000000000001, 2.0000001, 3));
    }

    @Test
    public void plus() {
        System.out.println(DoubleUtil.plus(1.0, 1.0, 1));
    }

    @Test
    public void subtract() {
        System.out.println(DoubleUtil.subtract(1.01111000000001, 1.0, 10));
    }
}
