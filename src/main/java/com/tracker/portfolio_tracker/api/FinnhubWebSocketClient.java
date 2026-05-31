package com.tracker.portfolio_tracker.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.portfolio_tracker.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FinnhubWebSocketClient {

    private final StockRepository stockRepository;
    private final TickDataStore tickDataStore;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${finnhub.api.key}")
    private String apiKey;

    private WebSocketClient wsClient;

    public void connect(List<String> tickers) {
        try {
            wsClient = new WebSocketClient(new URI("wss://ws.finnhub.io?token=" + apiKey)) {

                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Finnhub WebSocket connected");
                    for (String ticker : tickers) {
                        send("{\"type\":\"subscribe\",\"symbol\":\"" + ticker + "\"}");
                        System.out.println("Subscribed to: " + ticker);
                    }
                }

                @Override
                public void onMessage(String message) {
                    try {
                        JsonNode node = objectMapper.readTree(message);
                        if (!"trade".equals(node.get("type").asText())) return;

                        JsonNode data = node.get("data");
                        if (data == null || !data.isArray()) return;

                        for (JsonNode trade : data) {
                            String symbol = trade.get("s").asText();
                            BigDecimal price = trade.get("p").decimalValue();

                            stockRepository.findByTicker(symbol).ifPresent(stock -> {
                                stock.setCurrentPrice(price);
                                stock.setLastUpdated(LocalDateTime.now());
                                stockRepository.save(stock);
                            });
                            tickDataStore.addTick(symbol, price);
                        }
                    } catch (Exception e) {
                        System.err.println("WebSocket message error: " + e.getMessage());
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Finnhub WebSocket closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("Finnhub WebSocket error: " + ex.getMessage());
                }
            };

            wsClient.connect();
        } catch (Exception e) {
            System.err.println("WebSocket connection error: " + e.getMessage());
        }
    }

    public void subscribeTicker(String ticker) {
        if (wsClient != null && wsClient.isOpen()) {
            wsClient.send("{\"type\":\"subscribe\",\"symbol\":\"" + ticker + "\"}");
            System.out.println("Subscribed to: " + ticker);
        }
    }

    public void disconnect() {
        if (wsClient != null) {
            wsClient.close();
        }
    }
}