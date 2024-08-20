package com.agls.trading_service.infra.impl;

import com.agls.trading_service.infra.CurrenciesWebSocketService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CurrenciesWebSocketServiceImpl implements CurrenciesWebSocketService {

    @Value("${websocket.servers.currencies.url}")
    private String serverUrl;

    private Map<String, Double> currencies = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrenciesWebSocketServiceImpl.class);
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @PostConstruct
    public void serverConnection() {
        WebSocketClient client = new StandardWebSocketClient();

        try {
            client.execute(new TextWebSocketHandler() {
                @Override
                public void handleTextMessage(WebSocketSession session, org.springframework.web.socket.TextMessage message) {
                    try {
                        Map<String, Double> receivedCurrencies = objectMapper.readValue(message.getPayload(), new TypeReference<Map<String, Double>>() {});
                        currencies.putAll(receivedCurrencies);
                    } catch (Exception e) {
                        LOGGER.error("Error parsing JSON message: {}", e.getMessage());
                    }

                    notifyClients(currencies);
                }
            }, serverUrl);
        } catch(Exception ex) {
            LOGGER.error("Error while connecting to server: {}", ex.getMessage());
        }
    }

    private void notifyClients(Map<String, Double> currencies) {
        synchronized (sessions) {
            for (WebSocketSession session : sessions) {
                try {
                    session.sendMessage(new org.springframework.web.socket.TextMessage(objectMapper.writeValueAsString(currencies)));
                } catch (Exception e) {
                    LOGGER.error("Error sending message to client: {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }
}