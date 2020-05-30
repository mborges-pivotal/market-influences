package com.borgescloud.markets.news.admin;

import java.util.List;

import com.borgescloud.markets.news.model.Feed;
import com.borgescloud.markets.news.model.News;
import com.borgescloud.markets.news.service.FeedService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DBLoadRunner implements ApplicationRunner {

    private FeedService feedSrvc;

    @Autowired
    public DBLoadRunner(FeedService feedSrvc) {
        this.feedSrvc = feedSrvc;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<Feed> feeds = feedSrvc.findAll();
        for (Feed f : feeds) {
            List<News> articles = feedSrvc.findEntriesById(f.getId());
            log.info("Retrieve {} articles from {}", articles.size(), f.getName());
        }
        feedSrvc.index();

    }
    
}