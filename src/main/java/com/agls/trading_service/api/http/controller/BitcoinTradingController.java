package com.agls.trading_service.api.http.controller;

import com.agls.trading_service.api.http.dto.in.BitcoinTradeIn;
import com.agls.trading_service.api.http.dto.out.BitcoinTradeOut;
import com.agls.trading_service.domain.service.BitcoinTradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.agls.trading_service.api.http.mapper.BitcoinMapper.toBitcoinTradeOut;
import static com.agls.trading_service.api.http.mapper.BitcoinMapper.toModel;

@RestController
@RequestMapping("/bitcoin")
@RequiredArgsConstructor
public class BitcoinTradingController {

    private final BitcoinTradingService bitcoinTradingService;

    @PostMapping
    public ResponseEntity<BitcoinTradeOut> buyBitcoin(
            @RequestHeader("x-customerId") String customerId,
            @RequestBody @Valid BitcoinTradeIn bitcoinTradeIn) {

        var response = bitcoinTradingService.executeTrade(toModel(bitcoinTradeIn, customerId));

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(toBitcoinTradeOut(response));
    }
}
