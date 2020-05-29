package com.borgescloud.markets.news.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Feed {

    @Id
    private long id;
    private String name;
    private String url;
}