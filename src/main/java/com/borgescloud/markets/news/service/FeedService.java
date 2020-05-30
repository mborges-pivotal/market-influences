package com.borgescloud.markets.news.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import com.borgescloud.markets.news.model.Feed;
import com.borgescloud.markets.news.model.News;
import com.borgescloud.markets.news.repository.FeedRepository;
import com.borgescloud.markets.news.repository.NewsRepository;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/feed")
public class FeedService {

    private FullTextEntityManager fullTextEntityManager;

    private FeedRepository feedRepo;
    private NewsRepository newsRepo;

    @Autowired
    public FeedService(EntityManagerFactory entityManagerFactory, FeedRepository feedRepo, NewsRepository newsRepo) {
        fullTextEntityManager = Search.getFullTextEntityManager(entityManagerFactory.createEntityManager());
        this.feedRepo = feedRepo;
        this.newsRepo = newsRepo;
    }

    @GetMapping("/")
    public List<Feed> findAll() {
        return feedRepo.findAll();
    }

    @GetMapping("/count")
    public long count() {
        return feedRepo.count();
    }

    @GetMapping("/{id}")
    public Feed findById(@PathVariable long id) {
        return feedRepo.findById(id).orElseThrow(() -> new Error(String.format("Feed with id '%s' not found", id)));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("deleting feed with id {}", id);
        feedRepo.deleteById(id);
    }

    @GetMapping("/{id}/entries")
    public List<News> findEntriesById(@PathVariable long id) throws IOException {
        Feed feed = findById(id);

        News news = newsRepo.findTopByFeedIdOrderByPublishedDateDesc(id);
        ZonedDateTime recentNews = null;
        if (news != null) {
            recentNews = news.getPublishedDate();
            log.info("Most recent news from {}", recentNews);
        }

        URL feedSource = new URL(feed.getUrl());
        SyndFeedInput input = new SyndFeedInput();

        List<News> newsList = new ArrayList<News>();
        try {
            SyndFeed synd = input.build(new XmlReader(feedSource));

            List<SyndEntry> entries = synd.getEntries();
            for (SyndEntry e : entries) {

                Date d = e.getPublishedDate();
                if (d == null) {
                    continue;
                }

                ZonedDateTime newsPublishedDate = toZDT(e.getPublishedDate());
                if (news != null && newsPublishedDate.isBefore(recentNews.plusMinutes(1))) {
                    log.info("Skipping older news from most recent saved");
                    continue;
                }

                News n = News.builder().title(e.getTitle()).publishedDate(toZDT(e.getPublishedDate()))
                .description(getDescription(e.getDescription())).link(e.getLink()).feedId(id).build();
                newsList.add(n);
            }

            newsRepo.saveAll(newsList);
            // triggerIndexing();
            return newsRepo.findByFeedIdOrderByPublishedDateDesc(id);
        } catch (Exception e) {
            log.warn("Feed {} failed with {} error.", feed.getName(), e.getMessage());
            troubleshoot(feed);
            e.printStackTrace();
            return new ArrayList<News>();
        }
    }

    @GetMapping("/index")
    public void index() {
        triggerIndexing();
    }


    ///////////////////////////////////
    // Helper methods
    ///////////////////////////////////

    private void triggerIndexing() {
        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }    

    private ZonedDateTime toZDT(Date d) {
        return ZonedDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
    }

    private String getDescription(SyndContent d) {
        return (d != null ? d.getValue() : "");
    }

    private void troubleshoot(Feed feed) throws IOException {

        log.info("==================================================");
        log.info("==== {} ====", feed.getUrl());
        URL url = new URL(feed.getUrl());
        URLConnection urlcon = url.openConnection();

        // To get a map of all the fields of http header
        Map<String, List<String>> header = urlcon.getHeaderFields();

        // print all the fields along with their value.
        for (Map.Entry<String, List<String>> mp : header.entrySet()) {
            System.out.print(mp.getKey() + " : ");
            System.out.println(mp.getValue().toString());
        }
        System.out.println();
        System.out.println("Complete source code of the URL is-");
        System.out.println("---------------------------------");

        // get the inputstream of the open connection.
        BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
        String i;

        // print the source code line by line.
        while ((i = br.readLine()) != null) {
            System.out.println(i);
        }
        log.info("==================================================");
    }

}