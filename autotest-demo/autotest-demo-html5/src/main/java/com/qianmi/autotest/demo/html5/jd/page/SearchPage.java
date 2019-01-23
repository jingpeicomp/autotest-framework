package com.qianmi.autotest.demo.html5.jd.page;

import com.qianmi.autotest.html5.page.Html5Page;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

/**
 * 搜索页
 * Created by liuzhaoming on 2019/1/21.
 */
@Component
public class SearchPage extends Html5Page {
    @FindBy(id = "msKeyWord")
    private WebElement searchField;

    /**
     * 搜索商品
     *
     * @param keyword 关键词
     * @return 搜索结果页
     */
    public SearchResultPage search(String keyword) {
        wait(searchField).sendKeys(keyword);
        wait(searchField).sendKeys(Keys.ENTER);

        return gotoPage(SearchResultPage.class);
    }
}
