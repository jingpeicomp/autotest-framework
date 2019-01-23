package com.qianmi.autotest.app.testng;

import com.qianmi.autotest.app.common.AppPageTest;
import com.qianmi.autotest.app.common.AppResourceLoader;
import com.qianmi.autotest.base.common.BeanFactory;
import com.qianmi.autotest.base.testng.BaseTestRetryAnalyzer;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestResult;

/**
 * APP 测试失败重试监听器
 * Created by liuzhaoming on 2016/12/5.
 */
@Slf4j
public class AppTestRetryAnalyzer extends BaseTestRetryAnalyzer {
    /**
     * 重启APP
     */
    protected void restart(ITestResult result) {
        try {
            AppResourceLoader resourceContainer = BeanFactory.getBean(AppResourceLoader.class);
            resourceContainer.restartApp();
            AppPageTest pageTest = (AppPageTest) result.getInstance();
            pageTest.login(result.getMethod().getConstructorOrMethod().getMethod());
        } catch (Exception e) {
            log.error("TestRetryAnalyzer restart app fail {}", result.getMethod().getMethodName(), e);
        }
    }
}
