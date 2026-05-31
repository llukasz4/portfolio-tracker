package com.tracker.portfolio_tracker.api;

import com.tracker.portfolio_tracker.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class StooqClient {

    private final StockRepository stockRepository;
    private final WebClient webClient = WebClient.create("https://stooq.pl");

    public void updateStockPrice(String ticker) {
        try {
            String stooqTicker = ticker
                    .replace(".PL", "")
                    .replace(".WA", "")
                    .toLowerCase();

            String response = webClient.get()
                    .uri("/q/l/?s=" + stooqTicker + "&f=sd2t2ohlcv&h&e=csv")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) return;

            String[] lines = response.split("\n");
            if (lines.length < 2) return;

            String[] values = lines[1].split(",");
            if (values.length < 5) return;

            String priceStr = values[4].trim();
            if (priceStr.isEmpty() || priceStr.equals("N/D")) return;

            BigDecimal price = new BigDecimal(priceStr);

            stockRepository.findByTicker(ticker).ifPresent(stock -> {
                stock.setCurrentPrice(price);
                stock.setLastUpdated(LocalDateTime.now());
                stockRepository.save(stock);
            });

        } catch (Exception e) {
            System.err.println("Stooq error for " + ticker + ": " + e.getMessage());
        }
    }
}