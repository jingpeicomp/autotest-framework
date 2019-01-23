package com.qianmi.autotest.html5.testng;

import com.qianmi.autotest.appium.data.DataProvider;
import com.qianmi.autotest.base.common.AutotestUtils;
import com.qianmi.autotest.base.common.BeanFactory;
import com.qianmi.autotest.base.testng.BaseTestRetryAnalyzer;
import com.qianmi.autotest.html5.common.Html5ResourceLoader;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestResult;

import java.lang.reflect.Method;

/**
 * H5测试失败重试处理
 * Created by liuzhaoming on 2016/12/6.
 */
@Slf4j
public class Html5TestRetryAnalyzer extends BaseTestRetryAnalyzer {
    /**
     * 重启URL
     *
     * @param result 测试结果
     */
    @Override
    protected void restart(ITestResult result) {
        try {
            Method method = result.getMethod().getConstructorOrMethod().getMethod();
            String sceneName = AutotestUtils.getScene(method).value();
            DataProvider inputData = BeanFactory.getBean("inputData");
            String homeUrl = inputData.getProperty("homeUrl", sceneName);

            Html5ResourceLoader resourceContainer = BeanFactory.getBean(Html5ResourceLoader.class);
            resourceContainer.gotoPage(homeUrl);
        } catch (Exception e) {
            log.error("TestRetryAnalyzer restart app fail {}", result.getMethod().getMethodName(), e);
        }
    }
}
