package com.qianmi.autotest.web.testng;

import com.qianmi.autotest.base.common.AutotestUtils;
import com.qianmi.autotest.base.common.BeanFactory;
import com.qianmi.autotest.base.testng.BaseTestRetryAnalyzer;
import com.qianmi.autotest.web.common.WebResourceLoader;
import com.qianmi.autotest.web.data.DataProvider;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestResult;

import java.lang.reflect.Method;

/**
 * Web自动化测试程序失败重试接口
 * Created by liuzhaoming on 2016/12/8.
 */
@Slf4j
public class WebTestRetryAnalyzer extends BaseTestRetryAnalyzer {
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

            WebResourceLoader resourceContainer = BeanFactory.getBean(WebResourceLoader.class);
            log.info("Restart is called, and begin goto {}", homeUrl);
            resourceContainer.gotoPage(homeUrl);
        } catch (Exception e) {
            log.error("WebTestRetryAnalyzer restart app fail " + result.getMethod().getMethodName(), e);
        }
    }
}
