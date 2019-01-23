package com.qianmi.autotest.html5.page;

import com.qianmi.autotest.base.common.BeanFactory;
import com.qianmi.autotest.base.common.Logoutable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * APP页面门户
 * Created by liuzhaoming on 16/9/26.
 */
@Component
@Slf4j
public class Html5PageFacade extends Html5Page {
    /**
     * 退出当前登录用户
     */
    public void logout() {
        Logoutable logoutable = BeanFactory.getBeanByType(Logoutable.class);
        if (null != logoutable) {
            logoutable.logout();
        } else {
            log.warn("Logout cannot find Logoutable bean");
        }
    }
}
