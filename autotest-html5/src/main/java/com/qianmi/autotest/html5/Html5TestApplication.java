package com.qianmi.autotest.html5;

import com.qianmi.autotest.appium.testng.ScreenShotListener;
import com.qianmi.autotest.base.AbstractTestApplication;
import com.qianmi.autotest.base.testng.DefaultReporter;
import com.qianmi.autotest.base.testng.QmDingNotifier;
import com.qianmi.autotest.base.testng.TestRetryListener;
import com.qianmi.autotest.html5.testng.Html5TestRetryAnalyzer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.testng.ITestNGListener;

import java.util.ArrayList;
import java.util.List;

/**
 * H5测试主程序
 * Created by liuzhaoming on 2016/12/5.
 */
@SpringBootApplication(scanBasePackages = {"com.qianmi.autotest", "${autotest.scanPackage:}"})
public class Html5TestApplication extends AbstractTestApplication {
    public static void main(String[] args) {
        Html5TestApplication h5Application = new Html5TestApplication();
        h5Application.runTest(args);
    }

    /**
     * 子类可以重新此方法
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
            listeners.add(new TestRetryListener(Html5TestRetryAnalyzer.class));
        }

        if (Boolean.valueOf(System.getProperty("dingNotice"))) {
            listeners.add(new QmDingNotifier());
        }
        return listeners;
    }
}
