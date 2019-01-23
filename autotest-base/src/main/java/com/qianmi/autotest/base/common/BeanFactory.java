package com.qianmi.autotest.base.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * Spring Bean 工厂类
 * Created by liuzhaoming on 16/9/22.
 */
@Component
public class BeanFactory implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    /**
     * 根据Bean名称获取实例
     *
     * @return bean实例
     * @throws BeansException BeansException
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 根据类型获取实例
     *
     * @param type 类型
     * @return bean实例
     * @throws BeansException BeansException
     */
    public static <T> T getBean(Class<T> type) throws BeansException {
        String beanName = StringUtils.uncapitalize(type.getSimpleName());
        T bean = applicationContext.getBean(beanName, type);
        if (null != bean) {
            return bean;
        }
        return applicationContext.getBean(type);
    }

    /**
     * 根据类型获取Bean,可能存在多个事例,默认取第一个
     *
     * @param type 类型
     * @param <T>  泛型
     * @return bean实例
     * @throws BeansException BeansException
     */
    public static <T> T getBeanByType(Class<T> type) throws BeansException {
        Map<String, T> beanMap = applicationContext.getBeansOfType(type);
        if (beanMap.values().iterator().hasNext()) {
            return beanMap.values().iterator().next();
        }

        return null;
    }

    /**
     * 根据类型获取Bean列表
     *
     * @param type 类型
     * @param <T>  泛型
     * @return bean实例集合
     * @throws BeansException BeansException
     */
    public static <T> Collection<T> getBeansByType(Class<T> type) throws BeansException {
        Map<String, T> beanMap = applicationContext.getBeansOfType(type);

        return beanMap.values();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanFactory.applicationContext = applicationContext;
    }
}
