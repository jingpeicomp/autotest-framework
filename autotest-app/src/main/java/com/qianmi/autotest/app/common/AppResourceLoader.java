package com.qianmi.autotest.app.common;

import com.qianmi.autotest.appium.common.AppiumAutotestProperties;
import com.qianmi.autotest.appium.common.AppiumResourceLoader;
import com.qianmi.autotest.base.common.AutotestException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * 资源管理
 * Created by liuzhaoming on 16/9/29.
 */
@Component
@Slf4j
public class AppResourceLoader extends AppiumResourceLoader {
    /**
     * 重启Appium Server
     */
    public void restartApp() {
        try {
            AppiumDriver appiumDriver = getAppiumDriver();

            if (null == appiumDriver) {
                log.error("Restart app fail because appium server is null");
                return;
            }

            appiumDriver.closeApp();
            appiumDriver.launchApp();
        } catch (Exception e) {
            log.error("Cannot restart app", e);
        }
    }


    @Bean
    public AppiumDriver appiumDriver(AppAutotestProperties appProperties, AppiumAutotestProperties appiumProperties) {
        String appiumServerUrl = initAppiumServer(appiumProperties);
        String appiumServerAppFile = getActiveAppFile(appProperties, appiumProperties);
        Properties driverConfig = appiumProperties.getDriverConfig();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        for (String name : driverConfig.stringPropertyNames()) {
            setCapabilities(name, driverConfig.getProperty(name), capabilities);
        }

        if (StringUtils.isNotBlank(appiumServerAppFile)) {
            setCapabilities("app", appiumServerAppFile, capabilities);
        }

        try {
            AppiumDriver appiumDriver;
            if (driverConfig.getProperty("platformName", "Android").equalsIgnoreCase("ios")) {
                appiumDriver = new IOSDriver(new URL(appiumServerUrl), capabilities);
            } else {
                appiumDriver = new AndroidDriver(new URL(appiumServerUrl), capabilities);
            }
            return appiumDriver;
        } catch (Exception e) {
            log.error("Create appium driver fail {}", appiumServerUrl, e);
            throw new AutotestException("Create appium driver fail " + appiumServerUrl);
        }
    }

    @Bean
    @ConfigurationProperties("autotest")
    public AppAutotestProperties appAutotestProperties() {
        return new AppAutotestProperties();
    }

    /**
     * 获取当前生效的APP安装文件路径
     *
     * @param appProperties    app配置参数
     * @param appiumProperties appium配置参数
     * @return 当前生效的APP安装文件路径
     */
    private String getActiveAppFile(AppAutotestProperties appProperties, AppiumAutotestProperties appiumProperties) {
        String appFile = appProperties.getActiveAppFile();
        if (appiumProperties.isAppiumServerMngEnable()) {
            String appiumServerAppFile = queryAppFileInAppiumServer(appFile, appiumProperties);
            if (StringUtils.isNotBlank(appiumServerAppFile)) {
                return appiumServerAppFile;
            }

            return uploadAppFile(appFile, appiumProperties);
        }

        return appFile;
    }

    /**
     * 获取appium server上app安装文件路径
     *
     * @param appFile          本地apk文件地址
     * @param appiumProperties appium配置参数
     * @return appium server上app安装文件路径
     */
    private String queryAppFileInAppiumServer(String appFile, AppiumAutotestProperties appiumProperties) {
        try {
            String url = String.format("%s/appium/appfile", chooseAppiumServerMngUrl(appiumProperties));
            URI finalUrl = UriComponentsBuilder.fromUriString(url).queryParam("appfileName", appFile).build().toUri();
            Map responseBody = restTemplate.getForObject(finalUrl, Map.class);
            if (responseBody.containsKey("exist") && (boolean) responseBody.get("exist")) {
                return String.valueOf(responseBody.get("appfile"));
            }
        } catch (Exception e) {
            log.error("Fail to get appium server app file {}", appFile, e);
        }

        return "";
    }

    /**
     * 上次程序安装文件到appium server服务器
     *
     * @param appFile          本地apk文件地址
     * @param appiumProperties appium配置参数
     * @return appium server服务器安装文件路径
     */
    private String uploadAppFile(String appFile, AppiumAutotestProperties appiumProperties) {
        try {
            String url = String.format("%s/appium/appfile", chooseAppiumServerMngUrl(appiumProperties));
            FileSystemResource resource = new FileSystemResource(appFile);
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
            parts.add("Content-Type", MediaType.MULTIPART_FORM_DATA);
            parts.add("appfile", resource);

            ResponseEntity<Map> responseBody = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(parts), Map.class);
            if (responseBody.getStatusCode().is2xxSuccessful()) {
                return String.valueOf(responseBody.getBody().get("appfile"));
            }
        } catch (Exception e) {
            log.error("Fail to upload app file", e);
        }
        throw new AutotestException("Fail to upload app file");
    }
}
