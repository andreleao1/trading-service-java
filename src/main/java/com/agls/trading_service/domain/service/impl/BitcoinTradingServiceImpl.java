package com.agls.trading_service.domain.service.impl;

import com.agls.trading_service.domain.exceptions.TradeExecutionException;
import com.agls.trading_service.domain.models.BitcoinTradeModel;
import com.agls.trading_service.domain.service.BitcoinTradingService;
import com.agls.trading_service.infra.kafka.KafkaProducerGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class BitcoinTradingServiceImpl implements BitcoinTradingService {

    private final KafkaProducerGateway kafkaProducerGateway;

    @Value("${business.trade-fee}")
    private String tradeFee;

    @Retryable(
            value = { TradeExecutionException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000)
    )
    @CircuitBreaker(
            maxAttempts = 5,
            openTimeout = 10000,
            resetTimeout = 20000
    )
    @Override
    public String executeTrade(BitcoinTradeModel bitcoinTradeModel) {
        log.info("Initiating trading, Transaction id: {}", bitcoinTradeModel.getTradeId());
        applyTradeFee(bitcoinTradeModel);
        bitcoinTradeModel.setEffectiveBitcoinPurchased(calculateEffectiveBitcoinPurchased(bitcoinTradeModel));

        try {
            kafkaProducerGateway.sendToKafka(bitcoinTradeModel);
        } catch (Exception e) {
            log.error("Error processing trade.", e);
            throw new TradeExecutionException();
        }

        return bitcoinTradeModel.getTradeId().toString();
    }

    private void applyTradeFee(BitcoinTradeModel bitcoinTradeModel) {
        var tradeFeeValue = BigDecimal.valueOf(Double.parseDouble(tradeFee));
        bitcoinTradeModel.setDollarAmount(bitcoinTradeModel.getDollarAmount().subtract(tradeFeeValue));
    }

private BigDecimal calculateEffectiveBitcoinPurchased(BitcoinTradeModel bitcoinTradeModel) {
    log.info("Calculating effective bitcoin purchased, trade id: {}", bitcoinTradeModel.getTradeId());

    BigDecimal effectiveBitcoinPurchased = bitcoinTradeModel.getDollarAmount()
            .divide(bitcoinTradeModel.getBitcoinValue());

    log.info("Effective bitcoin purchased: {}, trade id: {}", effectiveBitcoinPurchased, bitcoinTradeModel.getTradeId());

    return effectiveBitcoinPurchased;
}
}
