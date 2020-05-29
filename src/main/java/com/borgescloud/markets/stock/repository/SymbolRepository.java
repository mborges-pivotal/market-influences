package com.borgescloud.markets.stock.repository;

import com.borgescloud.markets.stock.model.Symbol;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SymbolRepository extends JpaRepository<Symbol, Long> {
    
}