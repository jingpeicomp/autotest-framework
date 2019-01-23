package com.qianmi.autotest.demo.html5.jd.page;

import com.qianmi.autotest.html5.page.Html5Page;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * 带有下方导航条的页面
 * Created by liuzhaoming on 2019/1/21.
 */
public class NavigatePage extends Html5Page {
    @FindBy(id = "mCommonHome")
    private WebElement homePageButton;

    @FindBy(id = "分类")
    private WebElement categoryPageButton;

    @FindBy(id = "发现")
    private WebElement discoveryPageButton;

    @FindBy(id = "购物车")
    private WebElement cartPageButton;

    @FindBy(id = "我的")
    private WebElement minePageButton;

    /**
     * 前往购物车页面
     *
     * @return 购物车页面
     */
    public ShoppingCartPage gotoCartPage() {
        wait(minePageButton).click();

        return gotoPage(ShoppingCartPage.class);
    }

    /**
     * 前往首页
     *
     * @return 首页
     */
    public HomePage gotoHomePage() {
        wait(homePageButton).click();

        return gotoPage(HomePage.class);
    }
}
