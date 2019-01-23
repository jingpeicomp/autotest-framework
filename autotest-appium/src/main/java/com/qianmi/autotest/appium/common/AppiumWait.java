package com.qianmi.autotest.appium.common;

import com.qianmi.autotest.base.common.AutotestUtils;
import io.appium.java_client.AppiumDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * appium控件等待控制
 * Created by liuzhaoming on 2016/10/19.
 */
@Slf4j
public class AppiumWait {
    @Autowired
    private AppiumDriver appiumDriver;

    @Autowired
    private AppiumAutotestProperties autotestProperties;

    /**
     * 等待界面元素显示
     *
     * @param webElement webElement
     * @return webElement
     */
    @SuppressWarnings("unused")
    public WebElement wait(WebElement webElement) {
        if (log.isDebugEnabled()) {
            log.info("Begin wait WebElement {}", AutotestUtils.getWebElementDesc(webElement));
        }

        int maxCount = autotestProperties.getElementLoadTimeInMills() / autotestProperties.getRefreshIntervalInMills();
        for (int i = 0; i < maxCount; i++) {
            if (log.isDebugEnabled()) {
                log.info("Find WebElement {} for {} try", AutotestUtils.getWebElementDesc(webElement), i);
            }
            if (exist(webElement)) {
                if (log.isDebugEnabled()) {
                    log.info("Finish wait WebElement {}", AutotestUtils.getWebElementDesc(webElement));
                }
                return webElement;
            }

            sleep();
        }

        log.info("Cannot find WebElement {} within {} try", AutotestUtils.getWebElementDesc(webElement), maxCount);
        return webElement;
    }

    /**
     * 根据ID查询界面元素
     *
     * @param id 界面元素ID
     * @return webElement
     */
    public WebElement wait(String id) {
        log.info("Begin wait WebElement by id {}", id);
        int maxCount = autotestProperties.getElementLoadTimeInMills() / autotestProperties.getRefreshIntervalInMills();
        for (int i = 0; i < maxCount; i++) {
            log.info("Find WebElement by id {} for {} try", id, i);
            WebElement webElement = findById(id);
            if (null != webElement) {
                log.info("Finish wait WebElement by id {}", id);
                return webElement;
            }

            sleep();
        }

        log.info("Cannot find WebElement by id {} within {} try", id, maxCount);
        return null;
    }

    /**
     * 判断元素是否存在
     *
     * @param webElement 界面元素
     * @return boolean
     */
    private boolean exist(WebElement webElement) {
        long startTime = System.currentTimeMillis();
        try {
            webElement.isEnabled();
            return true;
        } catch (Exception e) {
            log.info("Exist WebElement spend time {} ms", System.currentTimeMillis() - startTime);
            return false;
        }
    }

    /**
     * 根据id查找元素
     *
     * @param id id
     * @return WebElement
     */
    private WebElement findById(String id) {
        try {
            return appiumDriver.findElement(By.id(id));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 当前线程sleep等待
     */
    private void sleep() {
        try {
            Thread.sleep(autotestProperties.getRefreshIntervalInMills());
        } catch (Exception ignored) {
        }
    }
}
