package com.qianmi.autotest.web.data;

import com.qianmi.autotest.web.common.WebAutotestProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 测试数据
 * Created by liuzhaoming on 16/9/27.
 */

@Data
@Slf4j
@Component
@ConfigurationProperties
@PropertySource("classpath:data.properties")
public class DataInitialization {

    private Properties input;

    private Properties output;

    private Properties browser;

    @Autowired
    private WebAutotestProperties webAutotestProperties;

    @Bean
    public DataProvider inputData() {
        Properties totalInputProperties = new Properties();
        if (null != input) {
            totalInputProperties.putAll(input);
        }

        String browserNamePrefix = webAutotestProperties.getActiveBrowserName() + ".input.";
        log.info("Input data bean browser name prefix is {}", browserNamePrefix);
        if (null != browser) {
            browser.stringPropertyNames().stream()
                    .filter(propertyName -> propertyName.startsWith(browserNamePrefix))
                    .forEach(propertyName ->
                            totalInputProperties.setProperty(propertyName.substring(browserNamePrefix.length()),
                                    browser.getProperty(propertyName))
                    );
        }
        return new DataProvider(totalInputProperties);
    }

    @Bean
    public DataProvider outputData() {
        Properties totalOutputProperties = new Properties();
        if (null != output) {
            totalOutputProperties.putAll(output);
        }

        String browserNamePrefix = webAutotestProperties.getActiveBrowserName() + ".output.";
        log.info("Output data bean browser name prefix is {}", browserNamePrefix);
        if (null != browser) {
            browser.stringPropertyNames().stream()
                    .filter(propertyName -> propertyName.startsWith(browserNamePrefix))
                    .forEach(propertyName ->
                            totalOutputProperties.setProperty(propertyName.substring(browserNamePrefix.length()),
                                    browser.getProperty(propertyName))
                    );
        }
        return new DataProvider(totalOutputProperties);
    }
}
