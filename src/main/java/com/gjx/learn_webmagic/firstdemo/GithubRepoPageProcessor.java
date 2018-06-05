/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.gjx.learn_webmagic.firstdemo;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author: guojixian
 * @create: 2018-06-05 10:18
 **/
public class GithubRepoPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {
        page.addTargetRequests(page.getHtml().links().regex("(https://www\\.jianshu\\.com/\\w+/\\w+)").all());
        page.putField("author", page.getUrl().regex("https://www\\.jianshu\\.com/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new GithubRepoPageProcessor())
                .addUrl("https://www.jianshu.com/p/f8ddef9846d7")
                .addPipeline(new JsonFilePipeline("classpath：\\result\\"))
                .thread(5)
                .run();
    }
}
