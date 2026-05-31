package com.tracker.portfolio_tracker.service;

import com.tracker.portfolio_tracker.api.FinnhubWebSocketClient;
import com.tracker.portfolio_tracker.repository.StockRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinnhubWebSocketService {

    private final FinnhubWebSocketClient webSocketClient;
    private final StockRepository stockRepository;

    @PostConstruct
    public void init() {
        List<String> tickers = stockRepository.findAll()
                .stream()
                .map(stock -> stock.getTicker())
                .toList();

        if (!tickers.isEmpty()) {
            webSocketClient.connect(tickers);
        } else {
            System.out.println("No stocks in portfolio — WebSocket not started");
        }
    }

    public void subscribeToNewTicker(String ticker) {
        webSocketClient.subscribeTicker(ticker);
    }

    @PreDestroy
    public void cleanup() {
        webSocketClient.disconnect();
    }
}