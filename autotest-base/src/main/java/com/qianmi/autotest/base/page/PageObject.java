package com.qianmi.autotest.base.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object 对象
 * Created by liuzhaoming on 2016/12/5.
 */
public interface PageObject {
    Logger LOGGER = LoggerFactory.getLogger(PageObject.class);

    /**
     * 线程阻塞,供页面渲染
     */
    default void sleep(int secTime) {
        if (secTime > 0) {
            try {
                Thread.sleep(secTime * 1000);
            } catch (InterruptedException e) {
                LOGGER.error("Page sleep fail", e);
            }
        }
    }

    /**
     * 线程阻塞,供页面渲染
     *
     * @param millTime 毫秒时间
     */
    default void sleepInMillTime(int millTime) {
        if (millTime > 0) {
            try {
                Thread.sleep(millTime);
            } catch (InterruptedException e) {
                LOGGER.error("Page sleep fail", e);
            }
        }
    }
}
