package com.qianmi.autotest.appium.page;

import com.qianmi.autotest.appium.common.AppiumAutotestProperties;
import com.qianmi.autotest.appium.common.AppiumWait;
import com.qianmi.autotest.base.common.BeanFactory;
import com.qianmi.autotest.base.page.PageObject;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import java.time.Duration;

/**
 * APP测试基类
 * Created by liuzhaoming on 16/9/23.
 */
@Slf4j
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AppiumBasePage implements PageObject {
    @Autowired
    protected AppiumDriver driver;

    @Autowired
    protected TouchAction touchAction;

    @Autowired
    protected AppiumAutotestProperties autotestProperties;

    @Autowired
    private AppiumWait appiumWait;

    /**
     * 页面跳转
     *
     * @param tClass Page Class
     * @param <T>    泛型
     * @return Page页面
     */
    public <T extends AppiumBasePage> T gotoPage(Class<T> tClass) {
        log.info("Begin goto page " + tClass.getName());
        T page = BeanFactory.getBean(tClass);
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(getPageInitTime())), page);
        page.afterConstruct();
        return page;
    }

    /**
     * 初始化页面
     *
     * @param page page
     * @param <T>  泛型
     * @return 页面
     */
    protected <T extends AppiumBasePage> T initPage(T page) {
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(getPageInitTime())), page);
        page.afterConstruct();
        return page;
    }

    /**
     * 线程阻塞,供页面渲染
     */
    protected void sleep() {
        sleepInMillTime(autotestProperties.getPageLoadTimeInMills());
    }

    /**
     * Page init time
     *
     * @return 默认值为1
     */
    protected int getPageInitTime() {
        return 1;
    }

    /**
     * Page元素构造完成后需要执行的操作
     */
    protected void afterConstruct() {

    }

    /**
     * 等待Page加载某个元素或者查询条件完成
     *
     * @param id 元素id
     * @return WebElement
     */
    protected WebElement wait(String id) {
        return appiumWait.wait(id);
    }

    /**
     * 等待Page加载某个元素
     *
     * @param webElement 页面元素
     * @return WebElement
     */
    protected WebElement wait(WebElement webElement) {
        return wait(webElement, autotestProperties.getElementLoadTimeInMills());
    }

    /**
     * 等待Page加载某个元素
     *
     * @param webElement     页面元素
     * @param timeOutInMills 最大等待时间,毫秒值
     * @return WebElement
     */
    protected WebElement wait(WebElement webElement, int timeOutInMills) {
        new WebDriverWait(driver, autotestProperties.getElementLoadTimeInMills() / 1000, autotestProperties.getRefreshIntervalInMills())
                .until(new ExpectedCondition<WebElement>() {
                    @Nullable
                    @Override
                    public WebElement apply(@Nullable WebDriver driver) {
                        if (isExist(webElement)) {
                            return webElement;
                        } else {
                            return null;
                        }
                    }
                });

        return webElement;
    }

    /**
     * 判断Page 元素是否存在
     *
     * @param webElement Page元素
     * @return boolean
     */
    protected boolean isExist(WebElement webElement) {
        if (null == webElement) {
            return false;
        }

        try {
            webElement.isDisplayed();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }


    /**
     * 判断Page 元素是否存在
     *
     * @param webElement     Page元素
     * @param timeOutInMills 超时时间，单位为毫秒
     * @return boolean
     */
    protected boolean isExist(WebElement webElement, int timeOutInMills) {
        if (null == webElement) {
            return false;
        }

        try {
            new WebDriverWait(driver, timeOutInMills / 1000, autotestProperties.getRefreshIntervalInMills())
                    .until(new ExpectedCondition<WebElement>() {
                        @Nullable
                        @Override
                        public WebElement apply(@Nullable WebDriver driver) {
                            if (isExist(webElement)) {
                                return webElement;
                            } else {
                                return null;
                            }
                        }
                    });

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 使用TouchAction滚动屏幕
     *
     * @param startX        起始点X坐标
     * @param startY        起始点Y坐标
     * @param finishX       结束点X坐标
     * @param finishY       结束点Y坐标
     * @param duringInMills 滚动时长(ms)
     */
    protected void swipe(int startX, int startY, int finishX, int finishY, int duringInMills) {
        PointOption startPoint = PointOption.point(startX, startY);
        PointOption finishPoint = PointOption.point(finishX, finishY);
        swipe(startPoint, finishPoint, duringInMills);
    }

    /**
     * 使用TouchAction滚动屏幕
     *
     * @param startPoint    起始点
     * @param finishPoint   结束点
     * @param duringInMills 滚动时长(ms)
     */
    protected void swipe(PointOption startPoint, PointOption finishPoint, int duringInMills) {
        touchAction.press(startPoint)
                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duringInMills)))
                .moveTo(finishPoint)
                .release()
                .perform();
    }

    /**
     * This Method for swipe up
     *
     * @param duringInMills 滑动速度 等待多少毫秒
     */
    protected void swipeUp(int duringInMills) {
        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        PointOption startPoint = PointOption.point(width / 2, height * 3 / 4);
        PointOption finishPoint = PointOption.point(width / 2, height / 4);
        swipe(startPoint, finishPoint, duringInMills);
    }

    /**
     * This Method for swipe down
     *
     * @param duringInMills 滑动速度 等待多少毫秒
     */
    protected void swipeDown(int duringInMills) {
        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        PointOption startPoint = PointOption.point(width / 2, height / 4);
        PointOption finishPoint = PointOption.point(width / 2, height * 3 / 4);
        swipe(startPoint, finishPoint, duringInMills);
    }

    /**
     * This Method for swipe Left
     *
     * @param duringInMills 滑动速度 等待多少毫秒
     */
    protected void swipeLeft(int duringInMills) {
        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        PointOption startPoint = PointOption.point(width * 3 / 4, height / 2);
        PointOption finishPoint = PointOption.point(width / 4, height / 2);
        swipe(startPoint, finishPoint, duringInMills);
    }

    /**
     * This Method for swipe right
     *
     * @param duringInMills 滑动速度 等待多少毫秒
     */
    protected void swipeRight(int duringInMills) {
        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        PointOption startPoint = PointOption.point(width / 4, height / 2);
        PointOption finishPoint = PointOption.point(width * 3 / 4, height / 2);
        swipe(startPoint, finishPoint, duringInMills);
    }

    /**
     * 是否是Android系统
     *
     * @return boolean
     */
    protected boolean isIosPlatform() {
        return driver instanceof IOSDriver;
    }

    /**
     * 是否是IOS系统
     *
     * @return boolean
     */
    protected boolean isAndroidPlatform() {
        return driver instanceof AndroidDriver;
    }
}
