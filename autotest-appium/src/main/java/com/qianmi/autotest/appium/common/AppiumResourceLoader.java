package com.qianmi.autotest.appium.common;

import com.qianmi.autotest.base.common.AutotestException;
import com.qianmi.autotest.base.common.BeanFactory;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Appium资源加载器
 * Created by liuzhaoming on 2016/12/6.
 */
@Slf4j
@SuppressWarnings("WeakerAccess")
public class AppiumResourceLoader {
    @Resource
    protected RestTemplate restTemplate;

    @Autowired
    private AppiumAutotestProperties appiumAutotestProperties;

    private String appiumServerMngUrl;

    @Bean
    @ConfigurationProperties("autotest")
    public AppiumAutotestProperties appiumAutotestProperties() {
        return new AppiumAutotestProperties();
    }

    /**
     * 系统关闭的时候关闭Appium连接
     */
    @PreDestroy
    public void stopAppiumServer() {
        log.info("Begin to stop appium server");
        if (!appiumAutotestProperties.isAppiumServerMngEnable()) {
            return;
        }

        Properties deviceConfig = appiumAutotestProperties.getDeviceConfig();
        String deviceIp = deviceConfig.getProperty("ip");
        String devicePort = deviceConfig.getProperty("port");
        String url = String.format("%s/appium/servers", appiumServerMngUrl);
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(url).queryParam("deviceIp", deviceIp);
        if (StringUtils.isNotBlank(devicePort)) {
            urlBuilder.queryParam("devicePort", devicePort);
        }

        URI deleteUrl = urlBuilder.build().toUri();

        try {
            restTemplate.delete(deleteUrl);
        } catch (Exception e) {
            log.error("Fail to stop appium server {} {}", deviceIp, devicePort, e);
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        return restTemplate;
    }

    @Bean
    public TouchAction touchAction(AppiumDriver driver) {
        return new TouchAction(driver);
    }

    @Bean
    public AppiumWait appiumWait() {
        return new AppiumWait();
    }

    @PostConstruct
    public void printStartLog() {
        log.warn("~~~~~||~~~~~ Start test by deviceName:{}", appiumAutotestProperties.getActiveDeviceName());
    }

    /**
     * 获取Appium 驱动
     *
     * @return Appium驱动
     */
    protected AppiumDriver getAppiumDriver() {
        AppiumDriver appiumDriver = BeanFactory.getBeanByType(AndroidDriver.class);
        if (null == appiumDriver) {
            appiumDriver = BeanFactory.getBeanByType(IOSDriver.class);
        }

        return appiumDriver;
    }

    /**
     * 远程启动一个Appium Server
     *
     * @param appiumAutotestProperties appium配置参数
     * @param deviceIp                 手机地址
     * @param devicePort               手机通讯端口
     * @return Appium Server URL
     */
    private String startAppiumServer(AppiumAutotestProperties appiumAutotestProperties, String deviceIp, String devicePort) {
        String url = String.format("%s/appium/servers", chooseAppiumServerMngUrl(appiumAutotestProperties));
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("deviceIp", deviceIp);
        if (StringUtils.isNotBlank(devicePort)) {
            requestBody.put("devicePort", devicePort);
        }

        try {
            Map response = restTemplate.postForObject(url, requestBody, Map.class);
            Object message = response.get("message");
            if (null != message) {
                String detail = null;
                if (message instanceof String) {
                    detail = (String) message;

                } else if (message instanceof Map) {
                    detail = (String) (((Map) message).get("detail"));
                }
                throw new AutotestException(String.format("Cannot start appium server deviceIp=%s  devicePort=%s " +
                        "detail=%s", deviceIp, devicePort, detail));
            }

            int appiumServerPort = ((Double) response.get("server_port")).intValue();
            String appiumServerIp = (String) response.get("server_ip");
            if (appiumServerPort == 0 || StringUtils.isBlank(appiumServerIp)) {
                log.error("Cannot start appium server {} {}", deviceIp, devicePort);
                throw new AutotestException(String.format("Cannot start appium server deviceIp=%s  devicePort=%s",
                        deviceIp, devicePort));
            }

            return String.format("http://%s:%s/wd/hub", appiumServerIp, appiumServerPort);
        } catch (Exception e) {
            log.error("Fail to start appium server {} {}", deviceIp, devicePort, e);
            throw new AutotestException(String.format("Fail to start appium server deviceIp=%s  devicePort=%s",
                    deviceIp, devicePort));
        }
    }

    /**
     * 获取Appium Server Manger服务器上正在运行的Appium server信息
     *
     * @param mngUrl Appium Server Manger服务器
     * @return 正在运行的Appium server信息
     */
    @SuppressWarnings("unchecked")
    private Map<String, ?> getAppiumServerInfo(String mngUrl) {
        String url = String.format("%s/appium/servers", mngUrl);
        try {
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            log.error("Fail to get appium server info from {}", mngUrl, e);
            return Collections.emptyMap();
        }
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

    /**
     * 选择一个连接数最小的appium mng server
     *
     * @param appiumAutotestProperties appium配置参数
     * @return appium mng server url
     */
    protected String chooseAppiumServerMngUrl(AppiumAutotestProperties appiumAutotestProperties) {
        if (null == appiumServerMngUrl) {
            synchronized (this) {
                if (null == appiumServerMngUrl) {
                    if (!appiumAutotestProperties.isAppiumServerMngEnable()) {
                        appiumServerMngUrl = "";
                    }

                    Properties deviceConfig = appiumAutotestProperties.getDeviceConfig();
                    String deviceIp = deviceConfig.getProperty("ip");
                    String devicePort = deviceConfig.getProperty("port", "5555");
                    String duid = String.format("%s:%s", deviceIp, devicePort);

                    String[] allMngUrls = appiumAutotestProperties.getAppiumServerMngUrl().split(",");
                    int minCount = 1000;
                    String curMngUrl = "";
                    for (String mngUrl : allMngUrls) {
                        Map<String, ?> appiumServerInfo = getAppiumServerInfo(mngUrl);
                        for (String appiumServerKey : appiumServerInfo.keySet()) {
                            if (appiumServerKey.equalsIgnoreCase(duid)) {
                                return mngUrl;
                            }
                        }

                        int curSize = appiumServerInfo.size();
                        if (minCount > curSize) {
                            minCount = curSize;
                            curMngUrl = mngUrl;
                        }
                    }

                    appiumServerMngUrl = curMngUrl;
                }
            }
        }

        return appiumServerMngUrl;
    }

    /**
     * 初始化appium server
     *
     * @param appiumAutotestProperties appium配置参数
     * @return appium server url
     */
    protected String initAppiumServer(AppiumAutotestProperties appiumAutotestProperties) {
        if (appiumAutotestProperties.isAppiumServerMngEnable()) {
            Properties deviceConfig = appiumAutotestProperties.getDeviceConfig();
            String deviceIp = deviceConfig.getProperty("ip");
            String devicePort = deviceConfig.getProperty("port");

            return startAppiumServer(appiumAutotestProperties, deviceIp, devicePort);
        }

        return appiumAutotestProperties.getAppiumServerUrl();
    }
}
