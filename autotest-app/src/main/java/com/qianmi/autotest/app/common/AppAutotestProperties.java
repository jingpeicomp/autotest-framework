package com.qianmi.autotest.app.common;

import lombok.Data;
import lombok.ToString;

/**
 * 配置文件,主要是处理框架的配置信息
 * Created by liuzhaoming on 16/9/27.
 */
@Data
@ToString(callSuper = true)
public class AppAutotestProperties {

    /**
     * 默认的APP文件
     */
    private String defaultAppFile;


    /**
     * 获取当前APP安装文件
     *
     * @return APP安装文件
     */
    public String getActiveAppFile() {
        return System.getProperty("appfile", defaultAppFile);
    }
}

