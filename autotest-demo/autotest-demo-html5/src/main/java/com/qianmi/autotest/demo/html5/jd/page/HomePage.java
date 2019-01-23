package com.qianmi.autotest.demo.html5.jd.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

/**
 * 京东H5首页
 * Created by liuzhaoming on 2019/1/21.
 */
@Component
public class HomePage extends NavigatePage {
    @FindBy(id = "msKeyWord")
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
