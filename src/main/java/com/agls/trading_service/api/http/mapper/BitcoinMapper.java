package com.agls.trading_service.api.http.mapper;

import com.agls.trading_service.api.http.dto.in.BitcoinTradeIn;
import com.agls.trading_service.api.http.dto.out.BitcoinTradeOut;
import com.agls.trading_service.domain.models.BitcoinTradeModel;

import java.math.BigDecimal;
import java.util.UUID;

public class BitcoinMapper {

    public static BitcoinTradeModel toModel(BitcoinTradeIn bitcoinTradeIn) {
        return BitcoinTradeModel.builder()
                .tradeId(UUID.randomUUID())
                .bitcoinValue(new BigDecimal(bitcoinTradeIn.getBitcoinValue()))
                .dollarAmount(new BigDecimal(bitcoinTradeIn.getDollarAmount()))
                .build();
    }

    public static BitcoinTradeOut toBitcoinTradeOut(String transactionId) {
        return BitcoinTradeOut.builder()
                .transactionId(transactionId)
                .build();
    }
}
