package com.borgescloud.markets.news.web;

import java.io.IOException;

import com.borgescloud.markets.news.service.FeedService;
import com.borgescloud.markets.news.service.NewsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NewsController {

    private FeedService feedSrvc;
    private NewsService newsSrvc;

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    public NewsController(FeedService feedSrvc, NewsService newsSrvc) {
        this.feedSrvc = feedSrvc;
        this.newsSrvc = newsSrvc;
    }

    @GetMapping("/")
    public String index(final Model model) {
        model.addAttribute("appName", applicationName);
        return "index";
    }

    @GetMapping("/feeds")
    public String feeds(final Model model) {
        model.addAttribute("appName", applicationName);
        model.addAttribute("pageName", "Feeds");
        model.addAttribute("feeds", feedSrvc.findAll());
        return "feeds";
    }

    @GetMapping("/news")
    public String news(@RequestParam("feedId") long feedId, final Model model) throws IOException {
        model.addAttribute("feed", feedSrvc.findById(feedId));
        model.addAttribute("feedEntries", feedSrvc.findEntriesById(feedId));
        return "news";
    }

    @GetMapping("/document")
    public String document(@RequestParam("id") long id, @RequestParam("feedId") long feedId, final Model model) throws IOException {
        model.addAttribute("feed", feedSrvc.findById(feedId));
        model.addAttribute("doc", newsSrvc.parseNews(id));
        return "document";
    }

    @GetMapping("/search")
    public String search(@RequestParam("name") String name, final Model model) throws IOException {
        model.addAttribute("results", newsSrvc.search(name));
        return "search";
    }

}