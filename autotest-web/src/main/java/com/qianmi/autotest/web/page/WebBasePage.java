package com.qianmi.autotest.web.page;

import com.qianmi.autotest.base.common.BeanFactory;
import com.qianmi.autotest.base.page.PageObject;
import com.qianmi.autotest.web.common.WebAutotestProperties;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;

/**
 * Web测试基类
 * Created by liuzhaoming on 16/9/23.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@Slf4j
public abstract class WebBasePage implements PageObject {
    @Autowired
    protected RemoteWebDriver driver;

    @Autowired
    private WebAutotestProperties webProperties;


    /**
     * 页面跳转
     *
     * @param tClass Page Class
     * @param <T>    泛型
     * @return Page页面
     */
    public <T extends WebBasePage> T gotoPage(Class<T> tClass) {
        log.info("Begin goto page " + tClass.getName());
        T page = BeanFactory.getBean(tClass);
        PageFactory.initElements(driver, page);
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
    protected <T extends WebBasePage> T initPage(T page) {
        PageFactory.initElements(driver, page);
        page.afterConstruct();
        return page;
    }

    /**
     * 线程阻塞,供页面渲染
     */
    protected void sleep() {
        sleepInMillTime(webProperties.getPageLoadTimeInMills());
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
        return new WebDriverWait(driver, webProperties.getElementLoadTimeInMills() / 1000, webProperties.getRefreshIntervalInMills())
                .until((ExpectedCondition<WebElement>) driver -> {
                    try {
                        return driver.findElement(By.id(id));
                    } catch (Exception e) {
                        return null;
                    }
                });
    }

    /**
     * 等待Page加载某个元素
     *
     * @param webElement 页面元素
     * @return WebElement
     */
    protected WebElement wait(WebElement webElement) {
        return wait(webElement, webProperties.getElementLoadTimeInMills());
    }

    /**
     * 等待Page加载某个元素
     *
     * @param webElement     页面元素
     * @param timeOutInMills 最大等待时间,毫秒值
     * @return WebElement
     */
    protected WebElement wait(WebElement webElement, int timeOutInMills) {
        new WebDriverWait(driver, timeOutInMills / 1000, webProperties.getRefreshIntervalInMills())
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
     * @param timeOutInMills 超时时间, 毫秒值
     * @return boolean
     */
    protected boolean isExist(WebElement webElement, int timeOutInMills) {
        if (null == webElement) {
            return false;
        }

        try {
            WebElement element = new WebDriverWait(driver, timeOutInMills, webProperties.getRefreshIntervalInMills())
                    .until((ExpectedCondition<WebElement>) driver -> {
                        if (isExist(webElement)) {
                            return webElement;
                        } else {
                            return null;
                        }
                    });

            return null != element;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 网页滚动到指定的位置
     *
     * @param xCoordinate x坐标
     * @param yCoordinate y坐标
     */
    protected void scrollTo(int xCoordinate, int yCoordinate) {
        String script = String.format("window.scrollBy(%d, %d)", xCoordinate, yCoordinate);
        driver.executeScript("window.scrollBy(0, 700)");
    }

    /**
     * 网页滚动到指定的元素位置
     *
     * @param webElement 元素
     */
    protected void scrollTo(WebElement webElement) {
        Coordinates coordinates = ((Locatable) webElement).getCoordinates();
        coordinates.inViewPort();
    }

    /**
     * 在控件上点击键盘Enter键
     *
     * @param webElement Web元素
     */
    protected void pressEnterKey(WebElement webElement) {
        if (null != webElement) {
            webElement.sendKeys(Keys.ENTER);
        }
    }

    /**
     * 后退到上个页面
     *
     * @param tClass 页面类
     * @param <T>    页面类
     * @return 上一个页面
     */
    protected <T extends WebBasePage> T back(Class<T> tClass) {
        driver.navigate().back();
        return gotoPage(tClass);
    }

    /**
     * 前进到下一个页面
     *
     * @param tClass 页面类
     * @param <T>    页面类
     * @return 下一个页面
     */
    protected <T extends WebBasePage> T forward(Class<T> tClass) {
        driver.navigate().forward();
        return gotoPage(tClass);
    }

    /**
     * 页面刷新
     *
     * @return 当前页面
     */
    @SuppressWarnings("unchecked")
    protected <T extends WebBasePage> T refresh() {
        driver.navigate().refresh();
        return (T) gotoPage(this.getClass());
    }

    /**
     * 向上滚动
     *
     * @param duringInMills 毫秒时间
     */
    protected void swipeUp(int duringInMills) {
        JavascriptExecutor jsExecutor = driver;
        int screenWebViewHeight = ((Long) jsExecutor.executeScript("return window.innerHeight || document.body.clientHeight")).intValue();
        jsExecutor.executeScript(String.format("window.scrollBy(0, %d)", screenWebViewHeight / 2));

        sleepInMillTime(duringInMills);
    }

    /**
     * 向下滚动
     *
     * @param duringInMills 毫秒时间
     */
    protected void swipeDown(int duringInMills) {
        JavascriptExecutor jsExecutor = driver;
        int screenWebViewHeight = ((Long) jsExecutor.executeScript("return window.innerHeight || document.body.clientHeight")).intValue();
        jsExecutor.executeScript(String.format("window.scrollBy(0, -%d)", screenWebViewHeight / 2));

        sleepInMillTime(duringInMills);
    }
}
