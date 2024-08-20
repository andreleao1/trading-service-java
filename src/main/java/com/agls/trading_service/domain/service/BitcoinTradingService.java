package com.agls.trading_service.domain.service;

import com.agls.trading_service.domain.models.BitcoinTradeModel;

public interface BitcoinTradingService {

    String executeTrade(BitcoinTradeModel bitcoinTradeModel);
}
