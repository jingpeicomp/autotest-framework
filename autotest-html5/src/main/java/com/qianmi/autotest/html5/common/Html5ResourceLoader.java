package com.qianmi.autotest.html5.common;

import com.qianmi.autotest.appium.common.AppiumAutotestProperties;
import com.qianmi.autotest.appium.common.AppiumResourceLoader;
import com.qianmi.autotest.appium.data.DataProvider;
import com.qianmi.autotest.base.common.AutotestException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Properties;

/**
 * 系统资源加载
 * Created by liuzhaoming on 2016/12/6.
 */
@Component
@Slf4j
@SuppressWarnings("unused")
public class Html5ResourceLoader extends AppiumResourceLoader {

    @Resource(name = "inputData")
    private DataProvider inputData;

    /**
     * 页面跳转
     *
     * @param url 要跳转的页面URL
     */
    public void gotoPage(String url) {
        try {
            AppiumDriver appiumDriver = getAppiumDriver();

            if (null == appiumDriver) {
                log.error("Goto page fail because appium server is null");
                return;
            }

            appiumDriver.get(url);
        } catch (Exception e) {
            log.error("Cannot gotoPage {}", url, e);
        }
    }

    @Bean
    public AppiumDriver appiumDriver(AppiumAutotestProperties appiumProperties) {
        String appiumServerUrl = initAppiumServer(appiumProperties);
        Properties driverConfig = appiumProperties.getDriverConfig();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        for (String name : driverConfig.stringPropertyNames()) {
            setCapabilities(name, driverConfig.getProperty(name), capabilities);
        }


        try {
            AppiumDriver appiumDriver;
            String browserName = driverConfig.getProperty("browserName");
            if (driverConfig.getProperty("platformName", "Android").equalsIgnoreCase("ios")) {
                if (StringUtils.isBlank(browserName)) {
                    setCapabilities("browserName", "Safari", capabilities);
                }

                appiumDriver = new IOSDriver(new URL(appiumServerUrl), capabilities);
            } else {
                if (StringUtils.isBlank(browserName)) {
                    setCapabilities("browserName", "Chrome", capabilities);
                }

                appiumDriver = new AndroidDriver(new URL(appiumServerUrl), capabilities);
                String startUrl = inputData.getProperty("startUrl");
                if (StringUtils.isNotBlank(startUrl)) {
                    appiumDriver.get(startUrl);
                }
            }
            return appiumDriver;
        } catch (Exception e) {
            log.error("Create appium driver fail {}", appiumServerUrl, e);
            throw new AutotestException("Create appium driver fail " + appiumServerUrl);
        }
    }
}
