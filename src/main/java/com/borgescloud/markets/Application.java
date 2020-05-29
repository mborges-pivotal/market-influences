package com.borgescloud.markets;

import java.util.List;

import com.borgescloud.markets.news.model.Feed;
import com.borgescloud.markets.news.model.News;
import com.borgescloud.markets.news.service.FeedService;
import com.borgescloud.markets.news.service.NewsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class Application implements ApplicationRunner {

	private FeedService feedSrvc;
    private NewsService newsSrvc;

    @Autowired
    public Application(FeedService feedSrvc, NewsService newsSrvc) {
        this.feedSrvc = feedSrvc;
        this.newsSrvc = newsSrvc;
    }	

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {

		List<Feed> feeds = feedSrvc.findAll();
		for(Feed f: feeds) {
			List<News> articles = feedSrvc.findEntriesById(f.getId());
			log.info("Retrieve {} articles from {}", articles.size(), f.getName());
		}
		feedSrvc.index();

	}

}
