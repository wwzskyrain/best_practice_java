/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package calculate;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author yueyi
 * @version : CalculatorTest.java, v 0.1 2022年03月17日 10:03 下午 yueyi Exp $
 * 1.Before的用法-初始化
 * 2.注意测试的message，要有业务含义。这才是测试case，而不只是为了覆盖代码.
 */
public class CalculatorTest {

    private Calculator calculator;

    @Before
    void setUp() throws Exception {
        calculator = new Calculator();
    }

    @Test
    void testMultiply() {
        assertEquals("Regular multiplication should work", calculator.multiply(4, 5), 20);
    }

    @Test
    void testMultiplyWithZero() {
        assertEquals("Multiple with zero should be zero", 0, calculator.multiply(0, 5));
        assertEquals("Multiple with zero should be zero", 0, calculator.multiply(5, 0));
    }
}