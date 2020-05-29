package com.borgescloud.markets.news.repository;

import com.borgescloud.markets.news.model.Feed;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long>  {

}