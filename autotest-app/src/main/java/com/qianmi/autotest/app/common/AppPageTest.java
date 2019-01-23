package com.qianmi.autotest.app.common;

import com.qianmi.autotest.app.AppTestApplication;
import com.qianmi.autotest.app.page.AppPageFacade;
import com.qianmi.autotest.appium.data.DataProvider;
import com.qianmi.autotest.base.common.AutotestUtils;
import com.qianmi.autotest.base.common.BasePageTest;
import com.qianmi.autotest.base.common.BeanFactory;
import com.qianmi.autotest.base.page.AppLoginPage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.Duration;

/**
 * Test基类
 * Created by liuzhaoming on 16/9/23.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@Slf4j
@SpringBootTest(classes = AppTestApplication.class)
public class AppPageTest extends BasePageTest {

    @Autowired
    protected AppPageFacade pageFacade;

    @Autowired
    protected AppiumDriver appiumDriver;

    @Autowired
    protected TouchAction touchAction;

    @Resource
    protected DataProvider inputData;

    @Resource
    protected DataProvider outputData;

    @Autowired
    private AppResourceLoader resourceContainer;

    /**
     * 注销用户
     */
    @AfterMethod
    public void logout() {
        try {
            pageFacade.logout();
        } catch (Exception e) {
            log.warn("Logout has error", e);
        }
    }

    @BeforeMethod
    public void login(Method method) {
        AppLoginPage loginPage = BeanFactory.getBeanByType(AppLoginPage.class);
        String sceneName = AutotestUtils.getSceneName(method);
        String userName = inputData.getProperty("userName", sceneName);
        String password = inputData.getProperty("password", sceneName);
        try {
            if (null != loginPage && StringUtils.isNoneBlank(userName)) {
                PageFactory.initElements(new AppiumFieldDecorator(appiumDriver, Duration.ofSeconds(1)), loginPage);
                loginPage.login(userName, password);
            }
        } catch (Exception e) {
            log.warn("Login has error", e);
            resourceContainer.restartApp();
            AutotestUtils.sleep(2000);
            try {
                if (StringUtils.isNoneBlank(userName)) {
                    loginPage.login(userName, password);
                }
            } catch (Exception ignored) {
            }
        }
    }
}
