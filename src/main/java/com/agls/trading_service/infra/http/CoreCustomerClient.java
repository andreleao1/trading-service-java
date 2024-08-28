package com.agls.trading_service.infra.http;

import com.agls.trading_service.infra.http.dto.request.ReserveBalanceRequest;
import com.agls.trading_service.infra.http.dto.response.WalletResponse;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(name = "core-customer-client", url = "${http.servers.core-customer.url}")
public interface CoreCustomerClient {

    @GetMapping(value = "/wallets/customer/{customerId}")
    Optional<WalletResponse> getWalletByCustomerId(@PathVariable String customerId);

    @PostMapping("/reserves")
    Response reserveBalance(@RequestBody ReserveBalanceRequest request);
}
