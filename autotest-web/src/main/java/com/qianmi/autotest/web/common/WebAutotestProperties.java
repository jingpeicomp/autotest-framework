package com.qianmi.autotest.web.common;

import com.qianmi.autotest.base.common.AutotestProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Web 测试程序配置参数
 * Created by liuzhaoming on 2016/12/6.
 */
@ConfigurationProperties
@Data
@Component
@Slf4j
public class WebAutotestProperties extends AutotestProperties {
    /**
     * driver配置属性
     */
    protected Properties driver;

    /**
     * browser配置属性
     */
    protected Properties browser;

    /**
     * 默认浏览器名称
     */
    protected String defaultBrowserName;

    /**
     * 设备配置信息
     */
    protected Properties browserConfig;

    /**
     * appium driver配置信息
     */
    protected Properties driverConfig;

    /**
     * 获取当前生效的设备名称
     *
     * @return 设备名称
     */
    public String getActiveBrowserName() {
        return System.getProperty("browserName", defaultBrowserName);
    }

    /**
     * 获取浏览器配置信息
     *
     * @return 浏览器配置信息
     */
    public Properties getBrowserConfig() {
        if (MapUtils.isEmpty(browserConfig)) {
            synchronized (this) {
                if (MapUtils.isEmpty(browserConfig)) {
                    String browserName = getActiveBrowserName();
                    Properties totalBrowserProperties = new Properties();
                    String browserNamePrefix = browserName + ".";
                    log.info("Browser config bean {}", browserNamePrefix);

                    browser.stringPropertyNames().stream()
                            .filter(propertyName -> propertyName.startsWith(browserNamePrefix))
                            .forEach(propertyName ->
                                    totalBrowserProperties.setProperty(propertyName.substring(browserNamePrefix.length()),
                                            browser.getProperty(propertyName))
                            );
                    browserConfig = totalBrowserProperties;
                }
            }
        }

        return browserConfig;
    }

    /**
     * 获取驱动配置信息
     *
     * @return 驱动配置信息
     */
    public Properties getDriverConfig() {
        if (MapUtils.isEmpty(driverConfig)) {
            synchronized (this) {
                if (MapUtils.isEmpty(driverConfig)) {
                    String browserName = getActiveBrowserName();
                    Properties totalDriverProperties = new Properties(driver);
                    String browserNamePrefix = browserName + ".driver.";
                    log.info("Driver config bean {}", browserNamePrefix);

                    if (MapUtils.isNotEmpty(browser)) {
                        browser.stringPropertyNames().stream()
                                .filter(propertyName -> propertyName.startsWith(browserNamePrefix))
                                .forEach(propertyName ->
                                        totalDriverProperties.setProperty(propertyName.substring(browserNamePrefix.length()),
                                                browser.getProperty(propertyName))
                                );
                    }
                    driverConfig = totalDriverProperties;
                }
            }
        }


        return driverConfig;
    }
}
