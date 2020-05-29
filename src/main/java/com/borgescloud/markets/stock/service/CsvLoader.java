package com.borgescloud.markets.stock.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import com.borgescloud.markets.stock.model.Exchange;
import com.borgescloud.markets.stock.repository.ExchangeRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CsvLoader implements ApplicationRunner {

    @Value("classpath:data/Finnhub_Exchanges.csv")
    private Resource exchangeFile;

    private ExchangeRepository exchangeRepo;

    @Autowired
    public CsvLoader(ExchangeRepository exchangeRepo) {
        this.exchangeRepo = exchangeRepo;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // parse CSV file to create a list of `User` objects
        Reader reader = new BufferedReader(new InputStreamReader(exchangeFile.getInputStream()));

        // create csv bean reader
        CsvToBean<Exchange> csvToBean = new CsvToBeanBuilder<Exchange>(reader)
                .withType(Exchange.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        // convert `CsvToBean` object to list of users
        List<Exchange> exchanges = csvToBean.parse();

        exchangeRepo.saveAll(exchanges);
        log.info("saved {} exchanges", exchanges.size());
    }

}