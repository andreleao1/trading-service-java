package com.agls.trading_service.domain.service;

import com.agls.trading_service.domain.exceptions.TradeExecutionException;
import com.agls.trading_service.domain.models.BitcoinTradeModel;
import com.agls.trading_service.domain.service.impl.BitcoinTradingServiceImpl;
import com.agls.trading_service.infra.kafka.KafkaProducerGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BitcoinTradingServiceImplTest {

    @Mock
    private KafkaProducerGateway kafkaProducerGateway;

    @InjectMocks
    private BitcoinTradingServiceImpl bitcoinTradingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bitcoinTradingService, "tradeFee", "10.0");
    }

    @Test
    void executeTrade_HappyPath() {
        var tradeId = UUID.randomUUID();

        BitcoinTradeModel tradeModel = new BitcoinTradeModel();
        tradeModel.setTradeId(tradeId);
        tradeModel.setDollarAmount(BigDecimal.valueOf(100.0));
        tradeModel.setBitcoinValue(BigDecimal.valueOf(50000.0));

        doNothing().when(kafkaProducerGateway).sendToKafka(tradeModel);

        String result = bitcoinTradingService.executeTrade(tradeModel);

        assertEquals(tradeId.toString(), result);
        assertEquals(BigDecimal.valueOf(1.8E-3), tradeModel.getEffectiveBitcoinPurchased());
        verify(kafkaProducerGateway, times(1)).sendToKafka(tradeModel);
    }

    @Test
    void executeTrade_ExceptionPath() {
        var tradeId = UUID.randomUUID();

        BitcoinTradeModel tradeModel = new BitcoinTradeModel();
        tradeModel.setTradeId(tradeId);
        tradeModel.setDollarAmount(BigDecimal.valueOf(100.0));
        tradeModel.setBitcoinValue(BigDecimal.valueOf(50000.0));

        doThrow(new RuntimeException("Kafka error")).when(kafkaProducerGateway).sendToKafka(tradeModel);

        assertThrows(TradeExecutionException.class, () -> bitcoinTradingService.executeTrade(tradeModel));
        verify(kafkaProducerGateway, times(1)).sendToKafka(tradeModel);
    }
}