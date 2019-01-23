package com.qianmi.autotest.base.testng;

import com.qianmi.autotest.base.common.BeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 钉钉通知执行结果
 * Created by liuzhaoming on 16/10/13.
 */
@Slf4j
public class QmDingNotifier implements ISuiteListener {

    private String testOutputDirectory = System.getProperty("testOutputDirectory");

    private String deviceName = System.getProperty("deviceName");

    private String operatorId = System.getProperty("operatorId");

    private String ip;

    public QmDingNotifier() {
        ip = getLocalHostIP();
    }

    /**
     * This method is invoked before the SuiteRunner starts.
     *
     * @param suite suite
     */
    @Override
    public void onStart(ISuite suite) {

    }

    /**
     * This method is invoked after the SuiteRunner has run all
     * the test suites.
     *
     * @param suite suite
     */
    @Override
    public void onFinish(ISuite suite) {
        if (needSendMessage()) {
            int skipCount = 0;
            int failCount = 0;
            for (ISuiteResult suiteResult : suite.getResults().values()) {
                skipCount += suiteResult.getTestContext().getSkippedTests().size();
                failCount += suiteResult.getTestContext().getFailedTests().size();
            }

            StringBuilder message = new StringBuilder();
            if (skipCount > 0 || failCount > 0) {//部分用例执行失败
                message.append(deviceName);
                message.append("自动化测试失败, 测试报告: ");
                message.append(getReportUrl());
            } else {
                message.append(deviceName);
                message.append("自动化测试通过, 测试报告: ");
                message.append(getReportUrl());
            }

            sendDingMsg(message.toString());
        }
    }

    /**
     * 判断是否需要发送订单消息
     *
     * @return boolean
     */
    private boolean needSendMessage() {
        return StringUtils.isNotBlank(testOutputDirectory) && StringUtils.isNotBlank(deviceName) && StringUtils
                .isNotBlank(ip);
    }

    /**
     * 获取本地IP地址
     *
     * @return 本地IP地址
     */
    private String getLocalHostIP() {
        String ip;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();
        } catch (Exception ex) {
            ip = "";
        }

        return ip;
    }

    /**
     * 获取测试报告的URL
     *
     * @return 测试报告URL
     */
    private String getReportUrl() {
        String[] temp = testOutputDirectory.split("/");
        String path = temp[temp.length - 1];
        return String.format("http://%s:8080/%s/%s-test-report.html", ip, path, deviceName);
    }

    /**
     * 发送钉钉通知
     *
     * @param message 通知内容
     */
    @SuppressWarnings("unchecked")
    private void sendDingMsg(String message) {
        RestTemplate restTemplate = BeanFactory.getBean(RestTemplate.class);
        if (null == restTemplate) {
            log.error("Cannot find RestTemplate instance");
            return;
        }

        if (StringUtils.isBlank(operatorId)) {
            log.error("Cannot find staffId");
            return;
        }

        try {
            String url = "http://ding.com/dingtalk/sendnotice?staffno={staffno}&content={content}";
            restTemplate.getForObject(url, Map.class, new HashMap() {
                {
                    put("staffno", operatorId);
                    put("content", message);
                }
            });

        } catch (Exception e) {
            log.error("Fail to send ding message {}", message, e);
        }
    }
}
