package com.qianmi.autotest.appium.testng;

import com.qianmi.autotest.base.common.BeanFactory;
import io.appium.java_client.AppiumDriver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 失败用例截屏
 * Created by liuzhaoming on 16/10/10.
 */
@Slf4j
public class ScreenShotListener implements ITestListener {
    /**
     * Invoked each time before a test will be invoked.
     * The <code>ITestResult</code> is only partially filled with the references to
     * class, method, start millis and status.
     *
     * @param result the partially filled <code>ITestResult</code>
     * @see ITestResult#STARTED
     */
    @Override
    public void onTestStart(ITestResult result) {

    }

    /**
     * Invoked each time a test succeeds.
     *
     * @param result <code>ITestResult</code> containing information about the run test
     * @see ITestResult#SUCCESS
     */
    @Override
    public void onTestSuccess(ITestResult result) {
    }

    /**
     * Invoked each time a test fails.
     *
     * @param result <code>ITestResult</code> containing information about the run test
     * @see ITestResult#FAILURE
     */
    @Override
    public void onTestFailure(ITestResult result) {
        log.info("onTestFailure is called");
        saveScreenShot(result);
    }

    /**
     * Invoked each time a test is skipped.
     *
     * @param result <code>ITestResult</code> containing information about the run test
     * @see ITestResult#SKIP
     */
    @Override
    public void onTestSkipped(ITestResult result) {
    }

    /**
     * Invoked each time a method fails but has been annotated with
     * successPercentage and this failure still keeps it within the
     * success percentage requested.
     *
     * @param result <code>ITestResult</code> containing information about the run test
     * @see ITestResult#SUCCESS_PERCENTAGE_FAILURE
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    /**
     * Invoked after the test class is instantiated and before
     * any configuration method is called.
     *
     * @param context context
     */
    @Override
    public void onStart(ITestContext context) {

    }

    /**
     * Invoked after all the tests have run and all their
     * Configuration methods have been called.
     *
     * @param context context
     */
    @Override
    public void onFinish(ITestContext context) {

    }

    private void saveScreenShot(ITestResult result) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String mDateTime = formatter.format(new Date());
        String fileName = mDateTime + "_" + result.getName();
        String filePath;
        try {
            AppiumDriver appiumDriver = BeanFactory.getBean(AppiumDriver.class);
            if (null == appiumDriver) {
                return;
            }
            File screenshot = appiumDriver.getScreenshotAs(OutputType.FILE);
            filePath = new File(result.getTestContext().getOutputDirectory()).getParentFile().getAbsolutePath() +
                    "/screenshot/" + fileName + ".jpg";
            File destFile = new File(filePath);
            FileUtils.copyFile(screenshot, destFile);

        } catch (Exception e) {
            log.error("Fail to screenshot {}", fileName, e);
            return;
        }

        if (StringUtils.isNoneBlank(filePath)) {
            Reporter.setCurrentTestResult(result);
            Reporter.log("Not passed test " + result.getName() + " screenshot is : ");
            //把截图写入到Html报告中方便查看
            Reporter.log("<img src=\"./screenshot/" + fileName + ".jpg\" width=\"400px\"/>");
        }
    }
}
