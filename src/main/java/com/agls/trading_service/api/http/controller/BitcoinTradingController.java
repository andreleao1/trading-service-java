package com.agls.trading_service.api.http.controller;

import com.agls.trading_service.api.http.dto.in.BitcoinTradeIn;
import com.agls.trading_service.api.http.dto.out.BitcoinTradeOut;
import com.agls.trading_service.domain.service.BitcoinTradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.agls.trading_service.api.http.mapper.BitcoinMapper.toBitcoinTradeOut;
import static com.agls.trading_service.api.http.mapper.BitcoinMapper.toModel;

@RestController
@RequestMapping("/bitcoin")
@RequiredArgsConstructor
public class BitcoinTradingController {

    private final BitcoinTradingService bitcoinTradingService;

    @PostMapping
    public ResponseEntity<BitcoinTradeOut> buyBitcoin(@RequestBody @Valid BitcoinTradeIn bitcoinTradeIn) {

        var response = bitcoinTradingService.executeTrade(toModel(bitcoinTradeIn));

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(toBitcoinTradeOut(response));
    }
}
