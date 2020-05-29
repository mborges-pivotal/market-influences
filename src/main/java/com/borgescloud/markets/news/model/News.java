package com.borgescloud.markets.news.model;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Indexed
public class News {

    @Id @GeneratedValue 
    private long id;

    private long feedId;

    @Field
    private String title;
    
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private ZonedDateTime publishedDate;
    
    @Lob
    @Field
    private String description;
    
    private String link;
}