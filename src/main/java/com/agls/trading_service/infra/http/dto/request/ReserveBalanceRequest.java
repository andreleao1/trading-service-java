package com.agls.trading_service.infra.http.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReserveBalanceRequest {

    private String walletId;
    private Double amount;
}
