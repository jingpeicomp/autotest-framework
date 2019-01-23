package com.qianmi.autotest.base.testng;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * 执行用例失败重试
 * Created by liuzhaoming on 16/10/10.
 */
@Slf4j
public abstract class BaseTestRetryAnalyzer implements IRetryAnalyzer {
    private int retryCount = 1;
    private static int maxRetryCount = NumberUtils.toInt(System.getProperty("maxRetryCount"), 1);

    /**
     * Returns true if the test method has to be retried, false otherwise.
     *
     * @param result The result of the test method that just ran.
     * @return true if the test method has to be retried, false otherwise.
     */
    @Override
    public boolean retry(ITestResult result) {
        restart(result);
        if (retryCount <= maxRetryCount) {
            log.info("Retry for {} on class {} for {} times", result.getName(), result.getTestClass().getName(),
                    retryCount);
            Reporter.setCurrentTestResult(result);
            Reporter.log("RunCount=" + (retryCount + 1));
            retryCount++;

            return true;
        }

        return false;
    }

    /**
     * 重启APP
     */
    protected abstract void restart(ITestResult result);
}
