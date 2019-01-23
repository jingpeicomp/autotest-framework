package com.qianmi.autotest.demo.app.jd.page;

import com.qianmi.autotest.app.page.AppBasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSFindBy;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

/**
 * 商品页
 * Created by liuzhaoming on 2018/12/26.
 */
@Component
public class ProductPage extends AppBasePage {

    @AndroidFindBy(id = "com.jd.lib.productdetail:id/pd_invite_friend")
    @iOSFindBy(accessibility = "加入购物车")
    private WebElement addCartButton;

    @AndroidFindBy(id = "com.jd.lib.productdetail:id/title_back")
    @iOSFindBy(accessibility = "返回")
    private WebElement backButton;

    @AndroidFindBy(id = "com.jd.lib.productdetail:id/detail_style_add_2_car")
    @iOSFindBy(accessibility = "确定")
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
     * 返回搜索列表页
     *
     * @return 搜索列表页
     */
    public SearchResultPage backToSearchResultPage() {
        wait(backButton).click();
        return gotoPage(SearchResultPage.class);
    }
}
