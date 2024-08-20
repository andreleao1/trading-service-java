package com.agls.trading_service.infra;

import jakarta.annotation.PostConstruct;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

public interface CurrenciesWebSocketService {

    @PostConstruct
    void serverConnection();

    void addSession(WebSocketSession session);

    void removeSession(WebSocketSession session);
}
