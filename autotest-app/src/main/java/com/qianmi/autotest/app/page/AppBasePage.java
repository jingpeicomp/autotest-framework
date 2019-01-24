package com.qianmi.autotest.app.page;

import com.qianmi.autotest.appium.page.AppiumBasePage;

/**
 * APP测试Page基类
 * Created by liuzhaoming on 2016/12/6.
 */
@SuppressWarnings("unused")
public abstract class AppBasePage extends AppiumBasePage {

    /**
     * This Method for swipe Left
     */
    protected void swipeLeft() {
        swipeLeft(autotestProperties.getSwipeTimeInMills());
    }

    /**
     * This Method for swipe up
     */
    protected void swipeUp() {
        swipeUp(autotestProperties.getSwipeTimeInMills());
    }


    /**
     * This Method for swipe down
     */
    protected void swipeToDown() {
        swipeDown(autotestProperties.getSwipeTimeInMills());
    }
}
