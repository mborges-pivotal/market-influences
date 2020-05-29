package com.borgescloud.markets.stock.repository;

import com.borgescloud.markets.stock.model.Exchange;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {
    
}