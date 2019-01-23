package com.qianmi.autotest.web.data;

import java.io.Serializable;
import java.util.Properties;

/**
 * 测试数据封装类
 * Created by liuzhaoming on 2016/11/14.
 */
@SuppressWarnings("unused")
public class DataProvider implements Serializable {
    private Properties originData;

    public DataProvider(Properties originData) {
        this.originData = originData;
    }

    public String getProperty(String name) {
        return originData.getProperty(name);
    }

    public String getProperty(String name, String sceneName) {
        String propertyName = String.format("scene.%s.%s", sceneName, name);
        if (originData.containsKey(propertyName)) {
            return originData.getProperty(propertyName);
        }

        return originData.getProperty(name);
    }
}
