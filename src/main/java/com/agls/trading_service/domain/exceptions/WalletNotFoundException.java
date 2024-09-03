package com.agls.trading_service.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WalletNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Was not possible to find the wallet to customer %s";

    public WalletNotFoundException(String customerId) {
        super(String.format(MESSAGE, customerId));
    }
}
