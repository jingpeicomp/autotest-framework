package com.qianmi.autotest.app.page;

import com.qianmi.autotest.appium.common.AppiumAutotestProperties;
import com.qianmi.autotest.appium.page.AppiumBasePage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * APP测试Page基类
 * Created by liuzhaoming on 2016/12/6.
 */
@SuppressWarnings("unused")
public abstract class AppBasePage extends AppiumBasePage {
    @Autowired
    private AppiumAutotestProperties appiumAutotestProperties;

    /**
     * This Method for swipe Left
     */
    protected void swipeLeft() {
        swipeLeft(appiumAutotestProperties.getSwipeTimeInMills());
    }

    /**
     * This Method for swipe up
     */
    protected void swipeUp() {
        swipeUp(appiumAutotestProperties.getSwipeTimeInMills());
    }


    /**
     * This Method for swipe down
     */
    protected void swipeToDown() {
        swipeDown(appiumAutotestProperties.getSwipeTimeInMills());
    }
}
