/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package calculate;

import calculate.service.CalculatorService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author yueyi
 * @version : MathApplicationTestWithSpy.java, v 0.1 2022年03月19日 5:16 下午 yueyi Exp $
 */
@RunWith(MockitoJUnitRunner.class)
public class MathApplicationTestWithSpy {

    @InjectMocks
    MathApplication mathApplication;
    CalculatorService calcService;

    class CalculatorServiceImpl implements CalculatorService {
        @Override
        public double add(double input1, double input2) {
            System.out.println("CalculatorServiceImpl.add()");
            return input1 + input2;
        }

        @Override
        public double subtract(double input1, double input2) {
            throw new UnsupportedOperationException("Method not implemented yet!");
        }

        @Override
        public double multiply(double input1, double input2) {
            throw new UnsupportedOperationException("Method not implemented yet!");
        }

        @Override
        public double divide(double input1, double input2) {
            throw new UnsupportedOperationException("Method not implemented yet!");
        }
    }

    @Before
    public void init() {
        calcService = spy(new CalculatorServiceImpl());
        mathApplication.setCalculatorService(calcService);
    }

    @Test
    public void test() {
        Assert.assertEquals(3.0, mathApplication.add(1.0, 2.0), 0.0003);
        verify(calcService, times(1)).add(1.0, 2.0);
        verify(calcService).add(1.0, 2.0);
    }

}