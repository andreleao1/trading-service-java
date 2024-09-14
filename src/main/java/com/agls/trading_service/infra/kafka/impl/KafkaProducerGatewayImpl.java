package com.agls.trading_service.infra.kafka.impl;

import com.agls.trading_service.domain.models.BitcoinTradeModel;
import com.agls.trading_service.infra.kafka.KafkaProducerGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaProducerGatewayImpl implements KafkaProducerGateway {

    @Value("${spring.kafka.topics.order-trading-topic}")
    private String topicName;

    private final KafkaTemplate<Object, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void sendToKafka(BitcoinTradeModel bitcoinTradeModel) {
        try {
            log.info("Sending message to Kafka topic: {}, trade id: {}", topicName, bitcoinTradeModel.getTradeId());
            var t = objectMapper.writeValueAsString(bitcoinTradeModel);
            kafkaTemplate.send(topicName, t);
            log.info("Message sent successfully.");
        } catch (Exception e) {
            log.error("Error sending message to Kafka topic: " + topicName, e);
            throw new RuntimeException("Error sending message to Kafka topic: " + topicName, e);
        }
    }
}
