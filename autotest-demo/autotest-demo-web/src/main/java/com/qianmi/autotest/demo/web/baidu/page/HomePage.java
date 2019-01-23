package com.qianmi.autotest.demo.web.baidu.page;

import com.qianmi.autotest.web.page.WebBasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

/**
 * Baidu首页
 * Created by liuzhaoming on 2019/1/22.
 */
@Component
public class HomePage extends WebBasePage {
    @FindBy(id = "kw")
    private WebElement inputField;

    @FindBy(id = "su")
    private WebElement submitButton;

    public SearchResultPage search(String keyword) {
        wait(inputField).sendKeys(keyword);
        wait(submitButton).click();

        return gotoPage(SearchResultPage.class);
    }
}
