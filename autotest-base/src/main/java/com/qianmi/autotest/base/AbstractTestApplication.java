package com.qianmi.autotest.base;

import com.qianmi.autotest.base.common.AutotestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITestNGListener;
import org.testng.TestNG;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Appium 应用程序基类
 * Created by liuzhaoming on 2016/12/6.
 */
@Slf4j
public abstract class AbstractTestApplication {

    @Autowired
    private WebDriver driver;

    /**
     * Run测试用例
     *
     * @param args 参数
     */
    protected void runTest(String[] args) {
        log.info("Run args is {}", Arrays.toString(args));
        AutotestUtils.initSystemProperties(args);

        TestNG testng = TestNG.privateMain(getTestNGArgs(), null);
        System.exit(testng.getStatus());
    }

    /**
     * 获取TestNG启动参数
     *
     * @return testng 启动参数
     */
    protected String[] getTestNGArgs() {
        String methodNames = String.join(",", AutotestUtils.getTestMethods());
        List<String> argList = new ArrayList<>();
        Collections.addAll(argList, "-methods", methodNames, "-usedefaultlisteners", "false");

        List<ITestNGListener> listeners = getListeners();
        if (CollectionUtils.isNotEmpty(listeners)) {
            String listenerStr = listeners.stream()
                    .map(listener -> listener.getClass().getName())
                    .collect(Collectors.joining(","));
            Collections.addAll(argList, "-listener", listenerStr);
        }

        String outputDir = System.getProperty("testOutputDirectory");
        if (StringUtils.isNotBlank(outputDir)) {
            Collections.addAll(argList, "-d", outputDir);
        }

        return argList.toArray(new String[0]);
    }

    /**
     * 子类可以重新此方法
     *
     * @return List<ITestNGListener> 监听器
     */
    protected abstract List<ITestNGListener> getListeners();

    /**
     * 系统关闭时关闭driver
     */
    @PreDestroy
    public void closeDriver() {
        if (null != driver) {
            driver.quit();
        }
    }
}
