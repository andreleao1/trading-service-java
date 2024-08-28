package com.agls.trading_service.domain.service.impl;

import com.agls.trading_service.domain.exceptions.WalletNotFoundException;
import com.agls.trading_service.domain.models.BitcoinTradeModel;
import com.agls.trading_service.domain.service.CoreCustomerService;
import com.agls.trading_service.infra.http.CoreCustomerClient;
import com.agls.trading_service.infra.http.dto.request.ReserveBalanceRequest;
import com.agls.trading_service.infra.http.dto.response.WalletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CoreCustomerServiceImpl implements CoreCustomerService {

    private final CoreCustomerClient coreCustomerClient;

    @Override
    public WalletResponse getWalletByCustomerId(String customerId) {
        log.info("Fetching wallet details for customer id: {}", customerId);

        return coreCustomerClient.getWalletByCustomerId(customerId).orElseThrow(() -> {
            log.error("Wallet not found for customer id: {}", customerId);
            return new WalletNotFoundException(customerId);
        });
    }

    @Override
    public void reserveBalance(BitcoinTradeModel bitcoinTradeModel, String walletId) {
        log.info("Reserving wallet balance to open trade request.");

        var requestBody = ReserveBalanceRequest.builder()
                .walletId(walletId)
                .amount(Double.parseDouble(bitcoinTradeModel.getDollarAmount().toString()))
                .build();

        var response = coreCustomerClient.reserveBalance(requestBody);

        if(response.status() != HttpStatus.CREATED.value()) {
            log.error("Error reserving balance for trade, status code: {}", response.status());
            throw new RuntimeException("Error reserving balance for trade.");
        }

        log.info("Balance reservation completed.");
    }
}
