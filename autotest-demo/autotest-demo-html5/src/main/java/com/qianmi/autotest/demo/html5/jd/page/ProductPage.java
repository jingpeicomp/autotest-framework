package com.qianmi.autotest.demo.html5.jd.page;

import com.qianmi.autotest.html5.page.Html5Page;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

/**
 * 商品详情页
 * Created by liuzhaoming on 2019/1/21.
 */
@Component
public class ProductPage extends Html5Page {
    @FindBy(id = "addCart2")
    private WebElement addCartButton;

    @FindBy(id = "gotoCart")
    private WebElement cartButton;

    @FindBy(id = "m_common_header_goback")
    private WebElement backButton;

    @FindBy(id = "popupConfirm")
    private WebElement okButton;

    /**
     * 将商品添加到购物车
     *
     * @return 当前页
     */
    public ProductPage addCart() {
        wait(addCartButton).click();
        if (isExist(okButton, autotestProperties.getElementLoadTimeInMills())) {
            okButton.click();
        }
        return this;
    }

    /**
     * 跳转到购物车页面
     *
     * @return 购物车页面
     */
    public ShoppingCartPage gotoCartPage() {
        wait(cartButton).click();
        //无实际意义,主要是演示时让观众看得清楚,实际场景可删除
        sleep(3);
        return gotoPage(ShoppingCartPage.class);
    }

    /**
     * 返回搜索列表页
     *
     * @return 搜索列表页
     */
    public SearchResultPage backToSearchResultPage() {
        wait(backButton).click();
        return gotoPage(SearchResultPage.class);
    }
}
