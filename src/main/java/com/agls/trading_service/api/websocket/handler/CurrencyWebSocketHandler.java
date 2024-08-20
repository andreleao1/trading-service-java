package com.agls.trading_service.api.websocket.handler;

import com.agls.trading_service.infra.CurrenciesWebSocketService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class CurrencyWebSocketHandler extends TextWebSocketHandler {

    private final CurrenciesWebSocketService currenciesWebSocketService;

    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyWebSocketHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        currenciesWebSocketService.addSession(session);
        LOGGER.info("New session connected: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        currenciesWebSocketService.removeSession(session);
        sessions.remove(session);
        LOGGER.info("Session disconnected: {}", session.getId());
    }
}

