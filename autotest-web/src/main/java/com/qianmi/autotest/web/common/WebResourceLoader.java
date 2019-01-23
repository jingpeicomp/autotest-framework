package com.qianmi.autotest.web.common;

import com.qianmi.autotest.base.common.AutotestException;
import com.qianmi.autotest.base.common.BeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * 系统资源加载
 * Created by liuzhaoming on 2016/12/6.
 */
@SuppressWarnings("WeakerAccess")
@Slf4j
@Component
public class WebResourceLoader {

    @Autowired
    private WebAutotestProperties webAutotestProperties;

    @Bean
    @ConfigurationProperties("autotest")
    public WebAutotestProperties webAutotestProperties() {
        return new WebAutotestProperties();
    }

    @PostConstruct
    public void printStartLog() {
        log.warn("~~~~~||~~~~~ Start test by browserName : {}", webAutotestProperties.getActiveBrowserName());
    }

    /**
     * 页面跳转
     *
     * @param url 要跳转的页面URL
     */
    public void gotoPage(String url) {
        try {
            WebDriver webDriver = getWebDriver();

            if (null == webDriver) {
                log.error("Goto page fail because web server is null");
                return;
            }

            webDriver.get(url);
        } catch (Exception e) {
            log.error("Cannot gotoPage {}", url, e);
        }
    }

    @Bean
    public WebDriver webDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        Properties driverConfig = webAutotestProperties.getDriverConfig();
        for (String name : driverConfig.stringPropertyNames()) {
            setCapabilities(name, driverConfig.getProperty(name), capabilities);
        }

        try {
            WebDriver webDriver;
            String browserName = webAutotestProperties.getActiveBrowserName();
            if (browserName.equalsIgnoreCase("Chrome")) {
                ChromeOptions options = new ChromeOptions().merge(capabilities);
                webDriver = new ChromeDriver(options);
            } else if (browserName.equalsIgnoreCase("Firefox")) {
                FirefoxOptions options = new FirefoxOptions().merge(capabilities);
                webDriver = new FirefoxDriver(options);
            } else if (browserName.equalsIgnoreCase("IE")) {
                InternetExplorerOptions options = new InternetExplorerOptions().merge(capabilities);
                webDriver = new InternetExplorerDriver(options);
            } else if (browserName.equalsIgnoreCase("Safari")) {
                SafariOptions options = new SafariOptions().merge(capabilities);
                webDriver = new SafariDriver(options);
            } else if (browserName.equalsIgnoreCase("Edge")) {
                EdgeOptions options = new EdgeOptions().merge(capabilities);
                webDriver = new EdgeDriver(options);
            } else {
                log.error("Create web driver fail, because the browserName is null or default.browserName is null");
                throw new AutotestException();
            }

//            webDriver.manage().window().maximize();
            return webDriver;
        } catch (Exception e) {
            log.error("Create web driver fail", e);
            throw new AutotestException("Create appium driver fail ");
        }
    }


    /**
     * 获取Selenium 驱动
     *
     * @return Selenium驱动
     */
    protected WebDriver getWebDriver() {
        return BeanFactory.getBeanByType(WebDriver.class);
    }

    /**
     * 设置配置属性
     *
     * @param name         属性名称
     * @param value        属性值
     * @param capabilities 配置容器
     */
    protected void setCapabilities(String name, String value, DesiredCapabilities capabilities) {
        if (null == value) {
            return;
        }

        capabilities.setCapability(name, value);
    }
}
