package com.agls.trading_service.infra.kafka;

import com.agls.trading_service.domain.models.BitcoinTradeModel;

public interface KafkaProducerGateway {

    void sendToKafka(BitcoinTradeModel bitcoinTradeModel);
}
