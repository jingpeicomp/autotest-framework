package com.qianmi.autotest.demo.app.jd.test;

import com.qianmi.autotest.app.common.AppPageTest;
import com.qianmi.autotest.demo.app.jd.page.HomePage;
import org.testng.annotations.Test;

/**
 * 京东商城APP测试用例
 * Created by liuzhaoming on 2018/12/25.
 */
public class JdAppTest extends AppPageTest {
    /**
     * 测试未登录情况下的商品搜索和浏览
     * 页面跳转流程：
     * 1. 进入首页，点击搜索框
     * 2. 进入搜索页，输入关键词并输入确定
     * 3. 搜索结果列表找到第一条商品并查看商品详情
     * 4. 点击返回按钮回到搜索结果页
     * 5. 点击首页回到首页
     */
    @Test(priority = 1)
    public void testSearchWhenLogout() {
        String mate9Name = inputData.getProperty("searchProductName");

        pageFacade.gotoPage(HomePage.class)
                .gotoSearchPage()
                .search(mate9Name)
                .viewFirstProduct()
                .backToSearchResultPage()
                .backToHomePage();
    }

    /**
     * 测试未登录将商品添加到购物车
     * * 页面跳转流程：
     * 1. 进入首页，点击搜索框
     * 2. 进入搜索页，输入关键词并输入确定
     * 3. 搜索结果列表找到第一条商品并添加到购物车
     * 4. 进入购物车页面
     */
    @Test(priority = 2)
    public void testAddCart() {
        String mate9Name = inputData.getProperty("searchProductName");

        pageFacade.gotoPage(HomePage.class)
                .gotoSearchPage()
                .search(mate9Name)
                .viewFirstProduct()
                .addCart()
                .backToSearchResultPage()
                .backToHomePage()
                .gotoCartPage();
    }
}
