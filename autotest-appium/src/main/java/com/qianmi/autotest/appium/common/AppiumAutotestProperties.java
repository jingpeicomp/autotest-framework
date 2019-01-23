package com.qianmi.autotest.appium.common;

import com.qianmi.autotest.base.common.AutotestProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.collections4.MapUtils;

import java.util.Properties;

/**
 * Appium 程序配置参数
 * Created by liuzhaoming on 2016/12/6.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppiumAutotestProperties extends AutotestProperties {
    /**
     * driver配置属性
     */
    protected Properties driver;

    /**
     * device配置属性
     */
    protected Properties device;

    /**
     * 默认device名称
     */
    protected String defaultDevice;

    /**
     * 是否连接Appium server Manager
     */
    protected boolean appiumServerMngEnable = false;

    /**
     * Appium server Manager地址,可以配置多个,中间用','分隔
     */
    protected String appiumServerMngUrl;

    /**
     * appium server地址,只有在不连接Appium server Manager情况下生效
     */
    protected String appiumServerUrl;

    /**
     * 设备配置信息
     */
    protected Properties deviceConfig;

    /**
     * appium driver配置信息
     */
    protected Properties driverConfig;

    /**
     * 获取当前生效的设备名称
     *
     * @return 设备名称
     */
    public String getActiveDeviceName() {
        return System.getProperty("deviceName", defaultDevice);
    }

    /**
     * 获取设备配置信息
     *
     * @return 设备配置信息
     */
    public Properties getDeviceConfig() {
        if (MapUtils.isEmpty(deviceConfig)) {
            synchronized (this) {
                if (MapUtils.isEmpty(deviceConfig)) {
                    String deviceName = getActiveDeviceName();
                    Properties totalDeviceProperties = new Properties();
                    String deviceNamePrefix = deviceName + ".";
                    String driverNamePrefix = deviceName + ".driver.";
                    device.stringPropertyNames().stream()
                            .filter(propertyName -> propertyName.startsWith(deviceNamePrefix) && !propertyName.startsWith(driverNamePrefix))
                            .forEach(propertyName ->
                                    totalDeviceProperties.setProperty(propertyName.substring(deviceNamePrefix.length()),
                                            device.getProperty(propertyName))
                            );
                    deviceConfig = totalDeviceProperties;
                }
            }
        }

        return deviceConfig;
    }

    /**
     * 获取appium driver配置信息
     *
     * @return appium driver配置信息
     */
    public Properties getDriverConfig() {
        if (MapUtils.isEmpty(driverConfig)) {
            synchronized (this) {
                if (MapUtils.isEmpty(driverConfig)) {
                    String deviceName = getActiveDeviceName();
                    Properties totalDriverProperties = new Properties(driver);
                    String driverNamePrefix = deviceName + ".driver.";
                    if (MapUtils.isNotEmpty(device)) {
                        device.stringPropertyNames().stream()
                                .filter(propertyName -> propertyName.startsWith(driverNamePrefix))
                                .forEach(propertyName ->
                                        totalDriverProperties.setProperty(propertyName.substring(driverNamePrefix.length()),
                                                device.getProperty(propertyName))
                                );
                    }
                    driverConfig = totalDriverProperties;
                }
            }
        }

        return driverConfig;
    }
}
