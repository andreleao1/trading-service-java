package com.agls.trading_service.domain.service;

import com.agls.trading_service.domain.exceptions.InsuficientBalanceException;
import com.agls.trading_service.domain.exceptions.TradeExecutionException;
import com.agls.trading_service.domain.models.BitcoinTradeModel;
import com.agls.trading_service.domain.service.impl.BitcoinTradingServiceImpl;
import com.agls.trading_service.infra.http.dto.response.WalletResponse;
import com.agls.trading_service.infra.kafka.KafkaProducerGateway;
import com.agls.trading_service.infra.repository.BitcoinTradeRepository;
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

    private static final String TRADE_FEE = "10.0";
    private static final BigDecimal DOLLAR_AMOUNT = BigDecimal.valueOf(100.0);
    private static final BigDecimal BITCOIN_VALUE = BigDecimal.valueOf(50000.0);
    private static final String SUFFICIENT_BALANCE = "200.0";
    private static final String INSUFFICIENT_BALANCE = "50.0";

    @Mock
    private KafkaProducerGateway kafkaProducerGateway;

    @Mock
    private CoreCustomerService coreCustomerService;

    @Mock
    private BitcoinTradeRepository tradeRepository;

    @InjectMocks
    private BitcoinTradingServiceImpl bitcoinTradingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bitcoinTradingService, "tradeFee", TRADE_FEE);
    }

    private BitcoinTradeModel createTradeModel(UUID tradeId, UUID customerId) {
        BitcoinTradeModel tradeModel = new BitcoinTradeModel();
        tradeModel.setTradeId(tradeId);
        tradeModel.setDollarAmount(DOLLAR_AMOUNT);
        tradeModel.setBitcoinValue(BITCOIN_VALUE);
        tradeModel.setCustomerId(customerId);
        return tradeModel;
    }

    private WalletResponse createWalletResponse(String balance) {
        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setBalance(balance);
        return walletResponse;
    }

    @Test
    void executeTrade_HappyPath() {
        var tradeId = UUID.randomUUID();
        var customerId = UUID.randomUUID();
        BitcoinTradeModel tradeModel = createTradeModel(tradeId, customerId);

        WalletResponse walletResponse = createWalletResponse(SUFFICIENT_BALANCE);

        when(coreCustomerService.getWalletByCustomerId(anyString())).thenReturn(walletResponse);
        when(coreCustomerService.reserveBalance(any(), anyString())).thenReturn("reserveId");
        doNothing().when(kafkaProducerGateway).sendToKafka(tradeModel);

        String result = bitcoinTradingService.executeTrade(tradeModel);

        assertEquals(tradeId.toString(), result);
        assertEquals(BigDecimal.valueOf(1.8E-3), tradeModel.getEffectiveBitcoinPurchased());
        verify(kafkaProducerGateway, times(1)).sendToKafka(tradeModel);
        verify(tradeRepository, times(1)).save(tradeModel);
    }

    @Test
    void executeTrade_InsufficientBalance() {
        var tradeId = UUID.randomUUID();
        var customerId = UUID.randomUUID();
        BitcoinTradeModel tradeModel = createTradeModel(tradeId, customerId);

        WalletResponse walletResponse = createWalletResponse(INSUFFICIENT_BALANCE);

        when(coreCustomerService.getWalletByCustomerId(anyString())).thenReturn(walletResponse);

        assertThrows(InsuficientBalanceException.class, () -> bitcoinTradingService.executeTrade(tradeModel));
        verify(kafkaProducerGateway, never()).sendToKafka(tradeModel);
        verify(tradeRepository, never()).save(tradeModel);
    }

    @Test
    void executeTrade_TradeExecutionException() {
        var tradeId = UUID.randomUUID();
        var customerId = UUID.randomUUID();
        BitcoinTradeModel tradeModel = createTradeModel(tradeId, customerId);

        WalletResponse walletResponse = createWalletResponse(SUFFICIENT_BALANCE);

        when(coreCustomerService.getWalletByCustomerId(anyString())).thenReturn(walletResponse);
        when(coreCustomerService.reserveBalance(any(), anyString())).thenReturn("reserveId");
        doThrow(new RuntimeException("Kafka error")).when(kafkaProducerGateway).sendToKafka(tradeModel);

        assertThrows(TradeExecutionException.class, () -> bitcoinTradingService.executeTrade(tradeModel));
        verify(kafkaProducerGateway, times(1)).sendToKafka(tradeModel);
        verify(tradeRepository, never()).save(tradeModel);
    }

    @Test
    void executeTrade_InvalidCustomerId() {
        var tradeId = UUID.randomUUID();
        BitcoinTradeModel tradeModel = createTradeModel(tradeId, null); // Invalid customer ID

        assertThrows(TradeExecutionException.class, () -> bitcoinTradingService.executeTrade(tradeModel));
        verify(kafkaProducerGateway, never()).sendToKafka(tradeModel);
        verify(tradeRepository, never()).save(tradeModel);
    }
}