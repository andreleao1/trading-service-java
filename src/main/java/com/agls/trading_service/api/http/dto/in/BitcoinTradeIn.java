package com.agls.trading_service.api.http.dto.in;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BitcoinTradeIn {

    @NotNull(message = "The bitcoin value is required")
    private String bitcoinValue;

    @NotNull(message = "The dollar amount is required")
    private String dollarAmount;
}
