package com.qianmi.autotest.web;

import com.qianmi.autotest.base.AbstractTestApplication;
import com.qianmi.autotest.base.testng.QmDingNotifier;
import com.qianmi.autotest.base.testng.TestRetryListener;
import com.qianmi.autotest.web.testng.ScreenShotListener;
import com.qianmi.autotest.web.testng.WebTestRetryAnalyzer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.testng.ITestNGListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Web自动化测试主程序入口
 * Created by liuzhaoming on 2016/12/7.
 */
@SpringBootApplication(scanBasePackages = {"com.qianmi.autotest", "${autotest.scanPackage:}"})
public class WebTestApplication extends AbstractTestApplication {
    public static void main(String[] args) {
        WebTestApplication webApplication = new WebTestApplication();
        webApplication.runTest(args);
    }

    /**
     * 子类可以重新此方法
     *
     * @return List<ITestNGListener> 监听器
     */
    @Override
    protected List<ITestNGListener> getListeners() {
        List<ITestNGListener> listeners = new ArrayList<>();

        if (Boolean.valueOf(System.getProperty("screenshot"))) {
            listeners.add(new ScreenShotListener());
        }

        if (Boolean.valueOf(System.getProperty("testRetry"))) {
            listeners.add(new TestRetryListener(WebTestRetryAnalyzer.class));
        }

        if (Boolean.valueOf(System.getProperty("dingNotice"))) {
            listeners.add(new QmDingNotifier());
        }
        return listeners;
    }
}
