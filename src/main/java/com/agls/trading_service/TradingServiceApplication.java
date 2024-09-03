package com.agls.trading_service;

import com.agls.trading_service.infra.repository.BitcoinTradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@EnableFeignClients
@EnableMongoRepositories
@SpringBootApplication
public class TradingServiceApplication {

	@Autowired
	BitcoinTradeRepository bitcoinTradeRepository;

	public static void main(String[] args) {
		SpringApplication.run(TradingServiceApplication.class, args);
	}

}
