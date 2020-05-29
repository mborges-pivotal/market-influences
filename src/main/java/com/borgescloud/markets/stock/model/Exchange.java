package com.borgescloud.markets.stock.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
@Entity
public class Exchange {

    @Id
    @GeneratedValue
    private long id;

    @CsvBindByName
    private String code;
    @CsvBindByName
    private String currency;
    @CsvBindByName
    private String name;
    
}