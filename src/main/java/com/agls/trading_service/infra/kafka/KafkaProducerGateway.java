package com.agls.trading_service.infra.kafka;

public interface KafkaProducerGateway {

    void sendToKafka(String bitcoinTradeModel);
}
