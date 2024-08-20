package com.agls.trading_service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BitcoinTradeModel {

    private UUID tradeId;

    private BigDecimal bitcoinValue;

    private BigDecimal dollarAmount;

    private BigDecimal effectiveBitcoinPurchased;

}
