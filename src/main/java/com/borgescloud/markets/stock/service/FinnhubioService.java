package com.borgescloud.markets.stock.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.borgescloud.markets.stock.model.Exchange;
import com.borgescloud.markets.stock.model.Symbol;
import com.borgescloud.markets.stock.repository.ExchangeRepository;
import com.borgescloud.markets.stock.repository.SymbolRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/finnhubio")
public class FinnhubioService {

    // https://finnhub.io/api/v1/stock/symbol?exchange=US&token=br7uhknrh5recjnuogo0

    private final String apiKey = "br7uhknrh5recjnuogo0";
    private final String baseUri = "https://finnhub.io/api/v1/";
    private final String stockSymbol = baseUri + "/stock/symbol?exchange=%s&token=%s";

    private ExchangeRepository exchangeRepo;
    private SymbolRepository symbolRepo;

    @Autowired
    public FinnhubioService(ExchangeRepository exchangeRepo, SymbolRepository symbolRepo) {
        this.exchangeRepo = exchangeRepo;
        this.symbolRepo = symbolRepo;
    }

    @RequestMapping(value = "/exchange", method = RequestMethod.GET)
    public List<Exchange> getExchanges() {
        return exchangeRepo.findAll();
    }

    @RequestMapping(value = "/stock/symbol", method = RequestMethod.GET)
    public List<Symbol> getStockSymbol(@RequestParam("exchange") String exchange) {

        if (symbolRepo.count() > 0) {
            log.info("return symbol list for exchange {} from database", exchange);
            return symbolRepo.findAll();
        }

        RestTemplate restTemplate = new RestTemplate();
        Symbol[] symbols = restTemplate.getForObject(String.format(stockSymbol, exchange, apiKey), Symbol[].class);

        List<Symbol> symbolList = Arrays.asList(symbols);
        symbolRepo.saveAll(symbolList);

        return symbolList;
    }
    
}