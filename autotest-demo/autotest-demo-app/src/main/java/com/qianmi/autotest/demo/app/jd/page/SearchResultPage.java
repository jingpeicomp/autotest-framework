package com.qianmi.autotest.demo.app.jd.page;

import com.qianmi.autotest.app.page.AppBasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSFindBy;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

/**
 * 商品搜索结果列表页
 * Created by liuzhaoming on 2018/12/25.
 */
@Component
public class SearchResultPage extends AppBasePage {

    @AndroidFindBy(id = "com.jd.lib.search:id/product_list_item")
    @iOSFindBy(accessibility = "com.jd.lib.search:id/product_list_item")
    private WebElement productItem;

    @AndroidFindBy(accessibility = "返回")
    @iOSFindBy(accessibility = "返回")
    private WebElement backButton;

    /**
     * 浏览列表首个商品详情
     *
     * @return 商品详情页
     */
    public ProductPage viewFirstProduct() {
        wait(productItem).click();
        return gotoPage(ProductPage.class);
    }

    /**
     * 返回APP首页
     *
     * @return APP首页
     */
    public HomePage backToHomePage() {
        wait(backButton).click();
        return gotoPage(HomePage.class);
    }
}
