package com.agls.trading_service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document("trades")
public class BitcoinTradeModel {

    @Id
    private UUID tradeId;

    private BigDecimal bitcoinValue;

    private BigDecimal dollarAmount;

    private BigDecimal effectiveBitcoinPurchased;

    private UUID customerId;

    private String reserveId;

}
