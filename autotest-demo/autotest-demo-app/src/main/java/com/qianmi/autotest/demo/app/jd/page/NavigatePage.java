package com.qianmi.autotest.demo.app.jd.page;

import com.qianmi.autotest.app.page.AppBasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSFindBy;
import org.openqa.selenium.WebElement;

/**
 * 带有下方导航条的页面
 * Created by liuzhaoming on 2018/12/26.
 */
public class NavigatePage extends AppBasePage {

    @AndroidFindBy(accessibility = "首页")
    @iOSFindBy(accessibility = "首页")
    private WebElement homeButton;

    @AndroidFindBy(accessibility = "分类")
    @iOSFindBy(accessibility = "分类")
    private WebElement categoryButton;

    @AndroidFindBy(accessibility = "发现")
    @iOSFindBy(accessibility = "发现")
    private WebElement discoveryButton;

    @AndroidFindBy(accessibility = "购物车")
    @iOSFindBy(accessibility = "购物车")
    private WebElement shoppingCartButton;

    @AndroidFindBy(accessibility = "我的")
    @iOSFindBy(accessibility = "我的")
    private WebElement mineButton;

    /**
     * 前往购物车页面
     *
     * @return 购物车页面
     */
    public ShoppingCartPage gotoCartPage() {
        wait(shoppingCartButton).click();
        return gotoPage(ShoppingCartPage.class);
    }

    /**
     * 前往首页
     *
     * @return 首页
     */
    public HomePage gotoHomePage() {
        wait(homeButton).click();
        return gotoPage(HomePage.class);
    }
}
