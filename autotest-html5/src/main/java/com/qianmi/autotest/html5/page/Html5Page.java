package com.qianmi.autotest.html5.page;

import com.qianmi.autotest.appium.page.AppiumBasePage;
import com.qianmi.autotest.base.common.AutotestUtils;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.offset.PointOption;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * H5页面基类
 * Created by liuzhaoming on 2016/12/6.
 */
@Slf4j
@SuppressWarnings({"unused", "WeakerAcess"})
public abstract class Html5Page extends AppiumBasePage {
    /**
     * 安卓控件边界正则表达式
     */
    private static final Pattern ANDROID_BOUNDS_PATTERN = Pattern.compile("\\[\\d+,\\d+\\]\\[\\d+,\\d+\\]");

    /**
     * IOS控件边界正则表达式
     */
    private static final Pattern IOS_BOUNDS_PATTERN = Pattern.compile("x=\"[\\d]+\" y=\"[\\d]+\" width=\"[\\d]+\" " +
            "height=\"[\\d]+\"");

    /**
     * 数值正则表达式
     */
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[\\d]+");

    private static final String NATIVE_APP = "NATIVE_APP";

    /**
     * 移动端WebView上方y坐标
     */
    private static int WEB_VIEW_TOP = 0;

    /**
     * 移动端WebView下方y坐标
     */
    private static int WEB_VIEW_BOTTOM = 0;

    @Autowired
    private TouchAction touchAction;

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
     * Page init time
     *
     * @return 默认值为1
     */
    protected int getPageInitTime() {
        return 5;
    }

    /**
     * 后退到上个页面
     *
     * @param tClass 页面类
     * @param <T>    页面类
     * @return 上一个页面
     */
    protected <T extends Html5Page> T back(Class<T> tClass) {
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
    protected <T extends Html5Page> T forward(Class<T> tClass) {
        driver.navigate().forward();

        return gotoPage(tClass);
    }

    /**
     * 页面刷新
     *
     * @return 当前页面
     */
    @SuppressWarnings("unchecked")
    protected <T extends Html5Page> T refresh() {
        driver.navigate().refresh();

        return (T) gotoPage(this.getClass());
    }

    /**
     * 判断Page 元素是否存在, 如果是WebView,元素不存在并不会抛出异常
     *
     * @param webElement Page元素
     * @return boolean
     */
    protected boolean isExist(WebElement webElement) {
        if (null == webElement) {
            return false;
        }

        try {
            return webElement.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Web原始click事件，将WebView元素位置转化位native位置进行点击，因为移动端H5存在300ms的点击延时，有些网站捕获了事件
     *
     * @param webElement webElement
     */
    protected void clickByNativePosition(WebElement webElement) {
        String originalContext = null;
        try {
            int screenWebViewWidth = ((Long) driver.executeScript("return window.innerWidth || document.body.clientWidth")).intValue();
            int screenWebViewHeight = ((Long) driver.executeScript("return window.innerHeight || document.body.clientHeight")).intValue();

            int elementWebViewX = webElement.getLocation().getX();
            int elementWebViewY = webElement.getLocation().getY();
            int height = webElement.getSize().getHeight();

            // Switching to Native view to use the native supported methods
            originalContext = driver.getContext();
            driver.context(NATIVE_APP);
            double screenWidth = driver.manage().window().getSize().getWidth();
//            double screenHeight = driver.manage().window().getSize().getHeight();
            double screenHeight = 1980;

            // Service URL bar height
            double serviceUrlBar = screenHeight * (0.135135);
            double relativeScreenViewHeight = screenHeight - serviceUrlBar;

            // From the WebView coordinates we will be calculating the native view coordinates using the width and
            // height
            int elementNativeViewX = (int) ((elementWebViewX * screenWidth) / screenWebViewWidth);
            double elementNativeViewY = ((elementWebViewY * relativeScreenViewHeight) / screenWebViewHeight);
            // Adding 1 just to remove the 0.9999999 error
            int relativeElementNativeViewY = (int) (elementNativeViewY + serviceUrlBar);

            if (driver instanceof IOSDriver) {
                touchAction.tap(PointOption.point(elementNativeViewX, relativeElementNativeViewY - height / 2)).perform();
            } else {
                touchAction.tap(PointOption.point(elementNativeViewX, relativeElementNativeViewY)).perform();
            }
        } catch (Exception e) {
            log.warn("Click web element has error {}", AutotestUtils.getWebElementDesc(webElement), e);
        } finally {
            if (null != originalContext) {
                driver.context(originalContext);
            }
        }
    }

    /**
     * Web按钮click事件, 将WebView元素位置转化位native位置进行点击，因为移动端H5存在300ms的点击延时，有些网站捕获了事件
     *
     * @param webElement webElement
     */
    protected void clickByNativeWebViewPosition(WebElement webElement) {
        String originalContext = null;
        try {
            int screenWebViewWidth = ((Long) driver.executeScript("return window.innerWidth || document.body.clientWidth")).intValue();
            int screenWebViewHeight = ((Long) driver.executeScript("return window.innerHeight || document.body.clientHeight")).intValue();

            int elementWebViewX = getWebElementCenterX(webElement);
            int elementWebViewY = getWebElementCenterY(webElement);

            // Switching to Native view to use the native supported methods
            originalContext = driver.getContext();
            driver.context(NATIVE_APP);

            Dimension size = driver.manage().window().getSize();
            double screenWidth = size.getWidth();

            if (driver instanceof IOSDriver) {
                calculateSafariWebViewY();
            } else {
                calculateChromeWebViewY();
            }

            int elementNativeViewX = (int) ((elementWebViewX * screenWidth) / screenWebViewWidth);
            int webViewNativeHeight = WEB_VIEW_BOTTOM - WEB_VIEW_TOP;
            int elementNativeViewY = WEB_VIEW_TOP + webViewNativeHeight * elementWebViewY /
                    screenWebViewHeight;
            touchAction.tap(PointOption.point(elementNativeViewX, elementNativeViewY)).perform();
        } catch (Exception e) {
            log.warn("Click web element {} has error ", AutotestUtils.getWebElementDesc(webElement), e);
        } finally {
            if (null != originalContext) {
                driver.context(originalContext);
            }
        }
    }

    /**
     * 通过native app 元素点击实现click效果
     *
     * @param webElement webElement
     */
    protected void clickNativeElement(WebElement webElement) {
        String originContext = driver.getContext();
        try {
            String nativeId = null;
            Field[] allFields = getClass().getDeclaredFields();
            for (Field field : allFields) {
                field.setAccessible(true);
                if (field.get(this) == webElement) {
                    FindBy annotation = field.getAnnotation(FindBy.class);
                    nativeId = annotation.id();
                    if (StringUtils.isBlank(nativeId)) {
                        String xpath = annotation.xpath();
                        String[] temps = xpath.replaceAll("[\\]\"]", "").split("\\[");
                        temps = temps[temps.length - 1].split("=");
                        nativeId = temps[temps.length - 1];
                    }
                    break;
                }
            }
            if (StringUtils.isBlank(nativeId)) {
                log.info("Cannot find native Id {}", nativeId);
                return;
            }

            if (!originContext.equals(NATIVE_APP)) {
                driver.context(NATIVE_APP);
            }

            driver.findElementById(nativeId).click();
        } catch (Exception e) {
            log.warn("nativeClick fail ", e);
        } finally {
            if (!originContext.equals(NATIVE_APP)) {
                driver.context(originContext);
            }
        }
    }

    /**
     * WebView 滚动
     *
     * @param during 毫秒时间
     */
    protected void webSwipeUp(int during) {
        int screenWebViewHeight = ((Long) driver.executeScript("return window.innerHeight || document.body" +
                ".clientHeight")).intValue();
        driver.executeScript(String.format("window.scrollBy(0, %d)", screenWebViewHeight / 2));

        sleepInMillTime(during);
    }

    /**
     * WebView 滚动
     *
     * @param during 毫秒时间
     */
    protected void webSwipeDown(int during) {
        int screenWebViewHeight = ((Long) driver.executeScript("return window.innerHeight || document.body" +
                ".clientHeight")).intValue();
        driver.executeScript(String.format("window.scrollBy(0, -%d)", screenWebViewHeight / 2));

        sleepInMillTime(during);
    }

    /**
     * 通过屏幕物理位置向左滑动
     *
     * @param webElement 元素
     * @param during     滑动时间
     */
    protected void swipeLeft(WebElement webElement, int during) {
        swipeLeft(webElement, during, 1);
    }

    /**
     * 通过屏幕物理位置向左滑动多次
     *
     * @param webElement    元素
     * @param duringInMills 滑动时间(ms)
     * @param times         滑动次数
     */
    protected void swipeLeft(WebElement webElement, int duringInMills, int times) {
        String originalContext = null;
        try {
            int screenWebViewWidth = ((Long) driver.executeScript("return window.innerWidth || document.body.clientWidth")).intValue();
            int screenWebViewHeight = ((Long) driver.executeScript("return window.innerHeight || document.body.clientHeight")).intValue();
            int elementWebViewX = getWebElementCenterX(webElement);
            int elementWebViewY = getWebElementCenterY(webElement);
            int elementWebWidth = webElement.getSize().getWidth();

            // Switching to Native view to use the native supported methods
            originalContext = driver.getContext();
            driver.context(NATIVE_APP);

            Dimension size = driver.manage().window().getSize();
            double screenWidth = size.getWidth();

            if (driver instanceof IOSDriver) {
                calculateSafariWebViewY();
            } else {
                calculateChromeWebViewY();
            }

            int elementNativeViewX = (int) ((elementWebViewX * screenWidth) / screenWebViewWidth);
            int webViewNativeHeight = WEB_VIEW_BOTTOM - WEB_VIEW_TOP;
            int elementNativeViewY = WEB_VIEW_TOP + webViewNativeHeight * elementWebViewY / screenWebViewHeight;
            int elementNativeWidth = (int) (elementWebWidth * screenWidth / screenWebViewWidth);

            PointOption startPoint = PointOption.point(elementNativeViewX + elementNativeWidth / 3, elementNativeViewY);
            PointOption finishPoint = PointOption.point(elementNativeViewX - elementNativeWidth / 3, elementNativeViewY);
            IntStream.range(0, times)
                    .forEach(i -> swipe(startPoint, finishPoint, duringInMills));

        } catch (Exception e) {
            log.warn("Click web element has error ", e);
        } finally {
            if (null != originalContext) {
                driver.context(originalContext);
            }
        }
    }

    /**
     * 通过屏幕物理位置向右滑动
     *
     * @param webElement    元素
     * @param duringInMills 滑动时间(ms)
     */
    protected void swipeRight(WebElement webElement, int duringInMills) {
        swipeRight(webElement, duringInMills);
    }

    /**
     * 通过屏幕物理位置向右滑动多次
     *
     * @param webElement    元素
     * @param duringInMills 滑动时间(ms)
     * @param times         滑动次数
     */
    protected void swipeRight(WebElement webElement, int duringInMills, int times) {
        String originalContext = null;
        try {
            int screenWebViewWidth = ((Long) driver.executeScript("return window.innerWidth || document.body.clientWidth")).intValue();
            int screenWebViewHeight = ((Long) driver.executeScript("return window.innerHeight || document.body.clientHeight")).intValue();
            int elementWebViewX = getWebElementCenterX(webElement);
            int elementWebViewY = getWebElementCenterY(webElement);
            int elementWebWidth = webElement.getSize().getWidth();

            // Switching to Native view to use the native supported methods
            originalContext = driver.getContext();
            driver.context(NATIVE_APP);

            Dimension size = driver.manage().window().getSize();
            double screenWidth = size.getWidth();

            if (driver instanceof IOSDriver) {
                calculateSafariWebViewY();
            } else {
                calculateChromeWebViewY();
            }

            int elementNativeViewX = (int) ((elementWebViewX * screenWidth) / screenWebViewWidth);
            int webViewNativeHeight = WEB_VIEW_BOTTOM - WEB_VIEW_TOP;
            int elementNativeViewY = WEB_VIEW_TOP + webViewNativeHeight * elementWebViewY / screenWebViewHeight;
            int elementNativeWidth = (int) (elementWebWidth * screenWidth / screenWebViewWidth);
            PointOption startPoint = PointOption.point(elementNativeViewX - elementNativeWidth / 3, elementNativeViewY);
            PointOption finishPoint = PointOption.point(elementNativeViewX + elementNativeWidth / 3, elementNativeViewY);

            IntStream.range(0, times).forEach(i -> swipe(startPoint, finishPoint, duringInMills));
        } catch (Exception e) {
            log.warn("Click web element has error ", e);
        } finally {
            if (null != originalContext) {
                driver.context(originalContext);
            }
        }
    }

    /**
     * This Method for swipe up
     *
     * @param duringInMills 滑动速度 等待多少毫秒
     */
    protected void swipeUp(int duringInMills) {
        String originContext = driver.getContext();
        if (!originContext.equals(NATIVE_APP)) {
            driver.context(NATIVE_APP);
        }

        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        swipe(width / 2, height * 3 / 4, width / 2, height / 4, duringInMills);

        if (!originContext.equals(NATIVE_APP)) {
            driver.context(originContext);
        }
    }

    /**
     * This Method for swipe down
     *
     * @param duringInMills 滑动速度 等待多少毫秒
     */
    protected void swipeDown(int duringInMills) {
        String originContext = driver.getContext();
        if (!originContext.equals(NATIVE_APP)) {
            driver.context(NATIVE_APP);
        }

        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        swipe(width / 2, height / 4, width / 2, height * 3 / 4, duringInMills);

        if (!originContext.equals(NATIVE_APP)) {
            driver.context(originContext);
        }
    }

    /**
     * This Method for swipe up, 从屏幕中心点滑到顶部
     *
     * @param duringInMills 滑动速度 等待多少毫秒
     */
    protected void swipeUpToTop(int duringInMills) {
        String originContext = driver.getContext();
        if (!originContext.equals(NATIVE_APP)) {
            driver.context(NATIVE_APP);
        }

        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        swipe(width / 2, height / 2, width / 2, 0, duringInMills);

        if (!originContext.equals(NATIVE_APP)) {
            driver.context(originContext);
        }
    }

    /**
     * This Method for swipe down， 从屏幕顶部滑到中心点
     *
     * @param duringInMills 滑动速度 等待多少毫秒
     */
    protected void swipeDownFromTop(int duringInMills) {
        String originContext = driver.getContext();
        if (!originContext.equals(NATIVE_APP)) {
            driver.context(NATIVE_APP);
        }

        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        swipe(width / 2, 0, width / 2, height / 2, duringInMills);

        if (!originContext.equals(NATIVE_APP)) {
            driver.context(originContext);
        }
    }

    /**
     * 获取控件中心点X坐标
     *
     * @param webElement webElement
     * @return 中心点X坐标
     */
    private int getWebElementCenterX(WebElement webElement) {
        if (webElement instanceof MobileElement) {
            return ((MobileElement) webElement).getCenter().getX();
        } else {
            int width = webElement.getSize().getWidth();
            return webElement.getLocation().getX() + width / 2;
        }
    }

    /**
     * 获取控件中心点Y坐标
     *
     * @param webElement webElement
     * @return 中心点Y坐标
     */
    private int getWebElementCenterY(WebElement webElement) {
        if (webElement instanceof MobileElement) {
            return ((MobileElement) webElement).getCenter().getY();
        } else {
            int height = webElement.getSize().getHeight();
            return webElement.getLocation().getY() + height / 2;
        }
    }

    /**
     * 计算chrome WebView Y 坐标
     */
    private void calculateChromeWebViewY() {
        if (WEB_VIEW_TOP > 0) {
            return;
        }

        synchronized (Html5Page.class) {
            String originContextName = driver.getContext();
            if (!originContextName.equals(NATIVE_APP)) {
                driver.context(NATIVE_APP);
            }

            try {
                String pageSource = driver.getPageSource();
                String[] temps = pageSource.split("<android.webkit.WebView ");
                if (temps.length != 2) {
                    return;
                }

                Matcher matcher = ANDROID_BOUNDS_PATTERN.matcher(temps[1]);
                if (matcher.find()) {
                    String boundsStr = matcher.group(0);
                    String[] coordinates = boundsStr.trim().replaceAll("\\[", "").replaceAll("\\]", ",").split(",");
                    WEB_VIEW_TOP = NumberUtils.toInt(coordinates[1], 0);
                    WEB_VIEW_BOTTOM = NumberUtils.toInt(coordinates[3], 0);

                    log.info("Chrome WebView y is {} - {}", WEB_VIEW_TOP, WEB_VIEW_BOTTOM);
                }
            } catch (Exception e) {
                log.warn("Cannot calculate chrome WebView y coordinates", e);
            } finally {
                if (!originContextName.equals(NATIVE_APP)) {
                    driver.context(originContextName);
                }
            }
        }

    }

    /**
     * 计算safari WebView Y 坐标
     */
    private void calculateSafariWebViewY() {
        if (WEB_VIEW_TOP > 0) {
            return;
        }

        synchronized (Html5Page.class) {
            String originContextName = driver.getContext();
            if (!originContextName.equals(NATIVE_APP)) {
                driver.context(NATIVE_APP);
            }

            try {
                String pageSource = driver.getPageSource();
                String[] temps = pageSource.split("<XCUIElementTypeWebView ");
                if (temps.length != 2) {
                    return;
                }

                Matcher matcher = IOS_BOUNDS_PATTERN.matcher(temps[1]);
                if (matcher.find()) {
                    String boundsStr = group(matcher, 1);
                    Matcher numberMatcher = NUMBER_PATTERN.matcher(boundsStr);

                    WEB_VIEW_TOP = NumberUtils.toInt(group(numberMatcher, 1), 0);
                    WEB_VIEW_BOTTOM = WEB_VIEW_TOP + NumberUtils.toInt(group(numberMatcher, 3), 0);

                    log.info("Safari WebView y is {} - {}", WEB_VIEW_TOP, WEB_VIEW_BOTTOM);
                }
            } catch (Exception e) {
                log.warn("Cannot calculate safari WebView y coordinates", e);
            } finally {
                if (!originContextName.equals(NATIVE_APP)) {
                    driver.context(originContextName);
                }
            }
        }

    }

    /**
     * 获取group值
     *
     * @param matcher matcher
     * @param index   index
     * @return 值
     */
    private String group(Matcher matcher, int index) {
        matcher.reset();
        int i = 0;
        while (matcher.find()) {
            String str = matcher.group();
            if (i++ == index) {
                return str;
            }
        }

        return "";
    }
}
