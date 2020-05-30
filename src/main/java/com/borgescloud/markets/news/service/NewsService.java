package com.borgescloud.markets.news.service;

import java.util.List;

import javax.persistence.EntityManager;

import com.borgescloud.markets.news.model.News;
import com.borgescloud.markets.news.model.NewsView;
import com.borgescloud.markets.news.model.NewsView.Content;
import com.borgescloud.markets.news.model.NewsView.Image;
import com.borgescloud.markets.news.model.NewsView.Import;
import com.borgescloud.markets.news.model.NewsView.Link;
import com.borgescloud.markets.news.model.NewsView.Media;
import com.borgescloud.markets.news.repository.NewsRepository;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/news")
public class NewsService {

    private NewsRepository newsRepo;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    public NewsService(NewsRepository newsRepo) {
        this.newsRepo = newsRepo;
    }

    @GetMapping("/")
    public List<News> findNews() {
        return newsRepo.findAll();
    }

    @GetMapping("/count")
    public long count() {
        return newsRepo.count();
    }

    @GetMapping("/{id}")
    public News findById(@PathVariable long id) {
        return newsRepo.findById(id).orElseThrow(() -> new Error(String.format("News with id '%s' not found", id)));
    }

    @DeleteMapping("/{id}")
    public void deleteNews(@PathVariable long id) {
        log.info("deleting news with id {}", id);
        newsRepo.deleteById(id);
    }

    @GetMapping("/{id}/parse")
    public NewsView parseNews(@PathVariable long id) {
        News n = findById(id);
        NewsView nv = NewsView.copy(n);
        parseNews(nv);
        return nv;
    }

    @GetMapping("/search")
    public List<News> search(@RequestParam("name") String name) {
        
        // Get the FullTextEntityManager
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        // Create a Hibernate Search DSL query builder for the required entity
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(News.class).get();

        // Generate a Lucene query using the builder
        Query query = queryBuilder.keyword().onField("title").andField("description").matching(name).createQuery();

        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(query,
                News.class);

        // returns JPA managed entities
        List<News> articles = fullTextQuery.getResultList();

        log.info("Found {} articles", articles.size());
        return articles;
        
    }

    ///////////////////////////////////
    // Helper methods
    ///////////////////////////////////

    private void parseNews(NewsView news) {
        log.info("===============================");
        try {
            Document doc = Jsoup.connect(news.getLink()).get();

            Elements body = doc.body().children();

            Elements media = doc.select("[src]");
            Elements links = doc.select("a[href]");
            Elements imports = doc.select("link[href]");
            Elements contentTags = doc.select("p, h1, h2, h3, h4, h5, h6");

            log.debug("Body: ({})", body.size());
            for (Element src : body) {
                if (src.hasAttr("src")) {
                    continue;
                }
                log.debug(" * {}: <{}>", src.tagName(), src.attr("class"));
            }

            log.debug("Content: ({})", contentTags.size());
            for (Element src : contentTags) {
                log.debug(" * {}: <{}>", src.tagName(), src.text());
                news.addContent(Content.builder().tag(src.tagName()).text(src.text()).build());
            }

            log.debug("Media: ({})", media.size());
            for (Element src : media) {

                if (src.normalName().equals("img")) {
                    log.debug(" * {}: <{}> {}x{} (%s)", src.tagName(), src.attr("abs:src"), src.attr("width"),
                            src.attr("height"), trim(src.attr("alt"), 20));
                    news.addMedia(Image.builder().tag(src.tagName()).src(src.attr("abs:src")).height(src.attr("height"))
                            .width(src.attr("width")).alt(src.attr("alt")).build());
                } else {
                    log.debug(" * {}: <{}>", src.tagName(), src.attr("abs:src"));
                    news.addMedia(Media.builder().tag(src.tagName()).src(src.attr("abs:src")).build());
                }
            } // for

            log.debug("Imports: ({})", imports.size());
            for (Element i : imports) {
                log.debug(" * {} <{}> ({})", i.tagName(), i.attr("abs:href"), i.attr("rel"));
                news.addImport(Import.builder().tag(i.tagName()).href(i.attr("abs:href")).rel(i.attr("rel")).build());
            }

            log.debug("Links: ({})", links.size());
            for (Element l : links) {
                log.debug(" * a: <{}>  ({})", l.attr("abs:href"), trim(l.text(), 35));
                news.addLink(Link.builder().tag(l.tagName()).text(trim(l.text(), 35)).href(l.attr("abs:href")).build());
            }

        } catch (HttpStatusException e1) {
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width - 1) + ".";
        else
            return s;
    }

}