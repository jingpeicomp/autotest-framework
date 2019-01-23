package com.qianmi.autotest.base.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 测试模块, 比如订单、购物车等,模块不划分输入输出数据
 * Created by liuzhaoming on 2016/12/6.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {
    String value();
}
