package com.agls.trading_service.domain.service;

import com.agls.trading_service.domain.models.BitcoinTradeModel;
import com.agls.trading_service.infra.http.dto.request.ReserveBalanceRequest;
import com.agls.trading_service.infra.http.dto.response.WalletResponse;

public interface CoreCustomerService {

    WalletResponse getWalletByCustomerId(String customerId);
    String reserveBalance(BitcoinTradeModel bitcoinTradeModel, String walletId);
}
