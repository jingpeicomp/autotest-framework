package com.qianmi.autotest.demo.html5.jd.page;

import com.qianmi.autotest.html5.page.Html5Page;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

/**
 * 搜索结果页面
 * Created by liuzhaoming on 2019/1/21.
 */
@Component
public class SearchResultPage extends Html5Page {
    @FindBy(id = "itemList")
    private WebElement firstProductItem;

    @FindBy(id = "msCancelBtn")
    private WebElement backButton;

    /**
     * 浏览列表首个商品详情
     *
     * @return 商品详情页
     */
    public ProductPage viewFirstProduct() {
        wait(firstProductItem).click();
        return gotoPage(ProductPage.class);
    }
}
