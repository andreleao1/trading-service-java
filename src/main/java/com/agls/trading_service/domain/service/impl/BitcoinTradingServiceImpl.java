package com.agls.trading_service.domain.service.impl;

import com.agls.trading_service.domain.exceptions.InsuficientBalanceException;
import com.agls.trading_service.domain.exceptions.TradeExecutionException;
import com.agls.trading_service.domain.models.BitcoinTradeModel;
import com.agls.trading_service.domain.service.BitcoinTradingService;
import com.agls.trading_service.domain.service.CoreCustomerService;
import com.agls.trading_service.infra.http.dto.response.WalletResponse;
import com.agls.trading_service.infra.kafka.KafkaProducerGateway;
import com.agls.trading_service.infra.repository.BitcoinTradeRepository;
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
    private final CoreCustomerService coreCustomerService;
    private final BitcoinTradeRepository tradeRepository;

    @Value("${business.trade-fee}")
    private String tradeFee;

    @Retryable(
            value = { TradeExecutionException.class },
            backoff = @Backoff(delay = 5000)
    )
    @CircuitBreaker(
            openTimeout = 10000,
            resetTimeout = 20000
    )
    @Override
    public String executeTrade(BitcoinTradeModel bitcoinTradeModel) {
        log.info("Initiating trading, Transaction id: {}", bitcoinTradeModel.getTradeId());

        try {
            WalletResponse walletResponse = coreCustomerService
                    .getWalletByCustomerId(bitcoinTradeModel.getCustomerId().toString());

            validateBalance(bitcoinTradeModel, walletResponse);

            String reserveId = makeReservation(bitcoinTradeModel, walletResponse);

            bitcoinTradeModel.setReserveId(reserveId);
            bitcoinTradeModel.setEffectiveBitcoinPurchased(calculateEffectiveBitcoinPurchased(bitcoinTradeModel));

            saveTradeAndSendToKafka(bitcoinTradeModel);
        } catch (Exception e) {
            log.error("Error processing trade, trade id: {}", bitcoinTradeModel.getTradeId(), e);
            throw new TradeExecutionException();
        }

        log.info("Trade executed successfully, trade id: {}", bitcoinTradeModel.getTradeId());
        return bitcoinTradeModel.getTradeId().toString();
    }

    private void validateBalance(BitcoinTradeModel bitcoinTradeModel, WalletResponse walletResponse) {
        if (!isEnoughBalance(bitcoinTradeModel, walletResponse)) {
            log.error("Insufficient balance for trade, trade id: {}", bitcoinTradeModel.getTradeId());
            throw new InsuficientBalanceException();
        }
    }

    private BigDecimal calculateEffectiveBitcoinPurchased(BitcoinTradeModel bitcoinTradeModel) {
        log.info("Calculating effective bitcoin purchased, trade id: {}", bitcoinTradeModel.getTradeId());

        BigDecimal effectiveBitcoinPurchased = bitcoinTradeModel.getDollarAmount()
                .divide(bitcoinTradeModel.getBitcoinValue());

        log.info("Effective bitcoin purchased: {}, trade id: {}",
                effectiveBitcoinPurchased,
                bitcoinTradeModel.getTradeId());

        return effectiveBitcoinPurchased;
    }

    private boolean isEnoughBalance(BitcoinTradeModel bitcoinTradeModel, WalletResponse walletResponse) {
        BigDecimal totalTradeValue = bitcoinTradeModel.getDollarAmount()
                .add(BigDecimal.valueOf(Double.parseDouble(tradeFee)));
        BigDecimal walletBalance = BigDecimal.valueOf(Double.parseDouble(walletResponse.getBalance()));

        return walletBalance.compareTo(totalTradeValue) >= 0;
    }

    private String makeReservation(BitcoinTradeModel bitcoinTradeModel, WalletResponse walletResponse) {
        return coreCustomerService.reserveBalance(bitcoinTradeModel, walletResponse.getId());
    }

    private void saveTradeAndSendToKafka(BitcoinTradeModel bitcoinTradeModel) {
        log.info("Saving trade to database, trade id: {}", bitcoinTradeModel.getTradeId());
        tradeRepository.save(bitcoinTradeModel);
        kafkaProducerGateway.sendToKafka(bitcoinTradeModel);
    }
}