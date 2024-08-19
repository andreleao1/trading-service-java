package com.agls.trading_service.api.websocket.config;

import com.agls.trading_service.api.websocket.handler.CurrencyWebSocketHandler;
import com.agls.trading_service.infra.CurrenciesWebSocketService;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CurrenciesWebSocketService currenciesWebSocketService;

    public WebSocketConfig(CurrenciesWebSocketService currenciesWebSocketService) {
        this.currenciesWebSocketService = currenciesWebSocketService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new CurrencyWebSocketHandler(currenciesWebSocketService), "/currencies")
                .setAllowedOrigins("*");
    }
}
