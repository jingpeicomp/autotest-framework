package com.qianmi.autotest.appium.data;

import com.qianmi.autotest.appium.common.AppiumAutotestProperties;
import lombok.Data;
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
@Component
@ConfigurationProperties
@PropertySource("classpath:data.properties")
public class DataInitialization {
    private Properties input;

    private Properties output;

    private Properties device;

    @Autowired
    private AppiumAutotestProperties appiumAutotestProperties;

    @Bean
    public DataProvider inputData() {
        Properties totalInputProperties = new Properties();
        if (null != input) {
            totalInputProperties.putAll(input);
        }

        if (null != device) {
            String deviceNamePrefix = appiumAutotestProperties.getActiveDeviceName() + ".input.";
            device.stringPropertyNames().stream()
                    .filter(propertyName -> propertyName.startsWith(deviceNamePrefix))
                    .forEach(propertyName ->
                            totalInputProperties.setProperty(propertyName.substring(deviceNamePrefix.length()),
                                    device.getProperty(propertyName))
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

        if (null != device) {
            String deviceNamePrefix = appiumAutotestProperties.getActiveDeviceName() + ".output.";
            device.stringPropertyNames().stream()
                    .filter(propertyName -> propertyName.startsWith(deviceNamePrefix))
                    .forEach(propertyName ->
                            totalOutputProperties.setProperty(propertyName.substring(deviceNamePrefix.length()),
                                    device.getProperty(propertyName))
                    );
        }
        return new DataProvider(totalOutputProperties);
    }
}
