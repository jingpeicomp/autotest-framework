package com.qianmi.autotest.web.page;

import com.qianmi.autotest.base.common.BeanFactory;
import com.qianmi.autotest.base.common.Logoutable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Web页面门户
 * Created by liuzhaoming on 16/9/26.
 */
@SuppressWarnings("unused")
@Slf4j
@Component
public class WebPageFacade extends WebBasePage {

    /**
     * 退出当前登录用户
     */
    public void logout() {
        Logoutable logoutable = BeanFactory.getBeanByType(Logoutable.class);
        if (null != logoutable) {
            logoutable.logout();
        } else {
            log.info("Cannot find Logoutable bean");
        }
    }
}
