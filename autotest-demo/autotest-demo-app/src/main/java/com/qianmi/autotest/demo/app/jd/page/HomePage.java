package com.qianmi.autotest.demo.app.jd.page;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSFindBy;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

/**
 * 京东APP首页
 * Created by liuzhaoming on 2018/12/25.
 */
@Component
public class HomePage extends NavigatePage {

    @AndroidFindBy(id = "com.jingdong.app.mall:id/a5f")
    @iOSFindBy(accessibility = "JDMainPage_input_gray")
    private WebElement searchButton;

    /**
     * 去搜索页
     *
     * @return 搜索页
     */
    public SearchPage gotoSearchPage() {
        wait(searchButton).click();
        return gotoPage(SearchPage.class);
    }
}
