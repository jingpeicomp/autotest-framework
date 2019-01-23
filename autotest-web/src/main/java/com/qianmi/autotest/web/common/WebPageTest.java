package com.qianmi.autotest.web.common;

import com.qianmi.autotest.base.common.AutotestUtils;
import com.qianmi.autotest.base.common.BasePageTest;
import com.qianmi.autotest.base.common.BeanFactory;
import com.qianmi.autotest.base.page.AppLoginPage;
import com.qianmi.autotest.web.WebTestApplication;
import com.qianmi.autotest.web.data.DataProvider;
import com.qianmi.autotest.web.page.WebPageFacade;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeMethod;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * Web网页测试类基类
 * Created by liuzhaoming on 2016/12/6.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@Slf4j
@SpringBootTest(classes = WebTestApplication.class)
public class WebPageTest extends BasePageTest {
    @Autowired
    protected WebPageFacade pageFacade;

    @Autowired
    protected WebDriver webDriver;

    @Resource
    protected DataProvider inputData;

    @Resource
    protected DataProvider outputData;

    @BeforeMethod
    public void login(Method method) {
        String sceneName = AutotestUtils.getSceneName(method);
        String userName = inputData.getProperty("userName", sceneName);
        String password = inputData.getProperty("password", sceneName);
        String startUrl = inputData.getProperty("startUrl", sceneName);
        try {
            log.info("Begin login startUrl={} userName={}", startUrl, userName);
            webDriver.get(startUrl);
            AutotestUtils.sleep(1000);
            AppLoginPage loginPage = BeanFactory.getBean(AppLoginPage.class);
            if (null != loginPage) {
                loginPage.login(userName, password);
            }
        } catch (Exception e) {
            log.warn("Login has error", e);
        }
    }
}
