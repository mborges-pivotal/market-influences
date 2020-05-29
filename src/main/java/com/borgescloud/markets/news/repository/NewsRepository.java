package com.borgescloud.markets.news.repository;

import java.util.List;

import com.borgescloud.markets.news.model.News;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long>  {

    News findTopByFeedIdOrderByPublishedDateDesc(long feedId);
    List<News> findByFeedIdOrderByPublishedDateDesc(long feedId);

}