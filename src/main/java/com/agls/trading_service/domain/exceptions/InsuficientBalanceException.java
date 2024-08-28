package com.agls.trading_service.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public class InsuficientBalanceException extends RuntimeException {

    private static final String message = "Insufficient balance to execute the trade";

    public InsuficientBalanceException() {
        super(message);
    }
}
