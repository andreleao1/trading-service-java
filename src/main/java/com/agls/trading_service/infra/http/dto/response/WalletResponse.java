package com.agls.trading_service.infra.http.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WalletResponse {

    private String id;

    private String customerId;

    private String balance;

    private String balanceInvested;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
