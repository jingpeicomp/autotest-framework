package com.qianmi.autotest.base.page;

/**
 * APP登录页面
 * Created by liuzhaoming on 16/9/26.
 */
public interface AppLoginPage {
    /**
     * 登录并跳转到首页
     *
     * @param username 用户名
     * @param password 密码
     */
    void login(String username, String password);
}
