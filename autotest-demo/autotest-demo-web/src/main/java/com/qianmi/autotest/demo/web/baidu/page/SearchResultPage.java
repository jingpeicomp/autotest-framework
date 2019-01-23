package com.qianmi.autotest.demo.web.baidu.page;

import com.qianmi.autotest.web.page.WebBasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

/**
 * Baidu搜索结果页面
 * Created by liuzhaoming on 2019/1/22.
 */
@Component
public class SearchResultPage extends WebBasePage {
    @FindBy(xpath = "//div[@id=\"content_left\"]/div[@id=\"1\"]")
    private WebElement firstItem;

    public boolean checkResult(String keyword) {
        return wait(firstItem).getText().contains(keyword);
    }
}
