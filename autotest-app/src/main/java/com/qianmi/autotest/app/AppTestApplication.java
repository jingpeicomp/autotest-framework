package com.qianmi.autotest.app;

import com.qianmi.autotest.app.testng.AppTestRetryAnalyzer;
import com.qianmi.autotest.appium.testng.ScreenShotListener;
import com.qianmi.autotest.base.AbstractTestApplication;
import com.qianmi.autotest.base.testng.DefaultReporter;
import com.qianmi.autotest.base.testng.QmDingNotifier;
import com.qianmi.autotest.base.testng.TestRetryListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.testng.ITestNGListener;

import java.util.ArrayList;
import java.util.List;

/**
 * APP自动化测试主程序入口
 * Created by liuzhaoming on 16/9/23.
 */
@SpringBootApplication(scanBasePackages = {"com.qianmi.autotest", "${autotest.scanPackage:}"})
public class AppTestApplication extends AbstractTestApplication {

    public static void main(String[] args) {
        AppTestApplication appApplication = new AppTestApplication();
        appApplication.runTest(args);
    }

    /**
     * 初始化TestNG监听器
     *
     * @return List<ITestNGListener> 监听器
     */
    @Override
    protected List<ITestNGListener> getListeners() {
        List<ITestNGListener> listeners = new ArrayList<>();
        listeners.add(new DefaultReporter());

        if (Boolean.valueOf(System.getProperty("screenshot"))) {
            listeners.add(new ScreenShotListener());
        }

        if (Boolean.valueOf(System.getProperty("testRetry"))) {
            listeners.add(new TestRetryListener(AppTestRetryAnalyzer.class));
        }

        if (Boolean.valueOf(System.getProperty("dingNotice"))) {
            listeners.add(new QmDingNotifier());
        }
        return listeners;
    }
}
