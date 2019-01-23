package com.qianmi.autotest.html5.common;

import com.qianmi.autotest.appium.data.DataProvider;
import com.qianmi.autotest.base.common.AutotestUtils;
import com.qianmi.autotest.base.common.BasePageTest;
import com.qianmi.autotest.base.common.BeanFactory;
import com.qianmi.autotest.base.page.AppLoginPage;
import com.qianmi.autotest.html5.Html5TestApplication;
import com.qianmi.autotest.html5.page.Html5PageFacade;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeMethod;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.Duration;

/**
 * H5 测试类基类
 * Created by liuzhaoming on 2016/12/6.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@SpringBootTest(classes = Html5TestApplication.class)
@Slf4j
public class Html5PageTest extends BasePageTest {

    @Autowired
    protected Html5PageFacade pageFacade;

    @Autowired
    protected AppiumDriver appiumDriver;

    @Resource
    protected DataProvider inputData;

    @Resource
    protected DataProvider outputData;

    @BeforeMethod
    public void login(Method method) {
        AppLoginPage loginPage = BeanFactory.getBeanByType(AppLoginPage.class);
        String sceneName = AutotestUtils.getSceneName(method);
        String userName = inputData.getProperty("userName", sceneName);
        String password = inputData.getProperty("password", sceneName);
        String startUrl = inputData.getProperty("startUrl", sceneName);

        try {
            appiumDriver.get(startUrl);
            AutotestUtils.sleep(1000);
            if (null != loginPage && StringUtils.isNoneBlank(userName)) {
                PageFactory.initElements(new AppiumFieldDecorator(appiumDriver, Duration.ofSeconds(1)), loginPage);
                loginPage.login(userName, password);
            }
        } catch (Exception e) {
            log.warn("Login has error", e);
            AutotestUtils.sleep(1000);
            try {
                if (null != loginPage && StringUtils.isNoneBlank(userName)) {
                    loginPage.login(userName, password);
                }
            } catch (Exception ignored) {
            }
        }
    }
}
