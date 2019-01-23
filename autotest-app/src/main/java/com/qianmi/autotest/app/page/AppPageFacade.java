package com.qianmi.autotest.app.page;

import com.qianmi.autotest.base.common.BeanFactory;
import com.qianmi.autotest.base.common.Logoutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * APP页面门户
 * Created by liuzhaoming on 16/9/26.
 */
@Component
public class AppPageFacade extends AppBasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppPageFacade.class);

    /**
     * 退出当前登录用户
     */
    public void logout() {
        Logoutable logoutable = BeanFactory.getBeanByType(Logoutable.class);
        if (null != logoutable) {
            logoutable.logout();
        } else {
            LOGGER.warn("Logout cannot find Logoutable bean");
        }
    }
}
