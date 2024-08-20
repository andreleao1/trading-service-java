package com.agls.trading_service.domain.exceptions;

public class TradeExecutionException extends RuntimeException {

    private static final String message = "An error occurred while executing the trade";

    public TradeExecutionException() {
        super(message);
    }
}
