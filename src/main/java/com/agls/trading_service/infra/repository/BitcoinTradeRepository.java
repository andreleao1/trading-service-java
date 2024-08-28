package com.agls.trading_service.infra.repository;

import com.agls.trading_service.domain.models.BitcoinTradeModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface BitcoinTradeRepository extends MongoRepository<BitcoinTradeModel, UUID> {
}
