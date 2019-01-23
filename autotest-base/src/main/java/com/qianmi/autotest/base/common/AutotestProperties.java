package com.qianmi.autotest.base.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 自动化测试参数
 * Created by liuzhaoming on 2019/1/7.
 */
@Data
public class AutotestProperties implements Serializable {
    /**
     * wait元素的总时间,单位为毫秒,超过此时间就认为超时
     */
    private int elementLoadTimeInMills = 10000;

    /**
     * wait元素的轮询间隔,单位为毫秒,即wait某个元素时,每隔多少时间取查看一次界面是否存在此元素
     */
    private int refreshIntervalInMills = 100;

    /**
     * 新页面加载后等待时间,单位为毫秒
     */
    private int pageLoadTimeInMills = 5000;

    /**
     * 页面左右滑动时间,单位为毫秒
     */
    private int swipeTimeInMills = 1000;

    /**
     * 测试程序中需要Spring扫描的包路径
     */
    private String scanPackage;
}
