package com.qianmi.autotest.demo.web.baidu.test;

import com.qianmi.autotest.demo.web.baidu.page.HomePage;
import com.qianmi.autotest.demo.web.baidu.page.SearchResultPage;
import com.qianmi.autotest.web.common.WebPageTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 百度Web页面测试
 * Created by liuzhaoming on 2019/1/22.
 */
public class BaiduWebTest extends WebPageTest {
    @Test
    public void searchBaidu() {
        String keyword = "百度";
        SearchResultPage searchResultPage = pageFacade.gotoPage(HomePage.class).search(keyword);
        Assert.assertTrue(searchResultPage.checkResult(keyword));
    }

    @Test(priority = 1)
    public void searchYouzhan() {
        String keyword = "有赞";
        SearchResultPage searchResultPage = pageFacade.gotoPage(HomePage.class).search(keyword);
        Assert.assertTrue(searchResultPage.checkResult(keyword));
    }
}
