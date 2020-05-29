package com.borgescloud.markets.stock.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Symbol {

    @Id
    @GeneratedValue
    private long id;

    private String symbol;
    private String displaySymbol;
    private String description;
    
}