package com.qianmi.autotest.base.common;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * Page测试类基类
 * Created by liuzhaoming on 2016/12/5.
 */
public abstract class BasePageTest extends AbstractTestNGSpringContextTests {
    @BeforeMethod
    public void beforeLog(Method method) {
        String methodName = method.getName();
        logger.info("**************** " + methodName + " started");
    }

    @AfterMethod
    public void afterLog(Method method) {
        String methodName = method.getName();
        logger.info("**************** " + methodName + " finished");
    }
}
