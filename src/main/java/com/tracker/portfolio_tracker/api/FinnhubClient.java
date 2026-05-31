package com.tracker.portfolio_tracker.api;

import com.tracker.portfolio_tracker.model.NewsArticle;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import com.tracker.portfolio_tracker.repository.StockRepository;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FinnhubClient {

    private final WebClient webClient = WebClient.create("https://finnhub.io/api/v1");
    private final StockRepository stockRepository;

    @Value("${finnhub.api.key}")
    private String apiKey;

    public List<NewsArticle> getNewsForTicker(String ticker, String companyName) {
        String today = java.time.LocalDate.now().toString();
        String yesterday = java.time.LocalDate.now().minusDays(1).toString();

        try {
            List<FinnhubNewsDTO> response = webClient.get()
                    .uri(uri -> uri.path("/company-news")
                            .queryParam("symbol", ticker)
                            .queryParam("from", yesterday)
                            .queryParam("to", today)
                            .queryParam("token", apiKey)
                            .build())
                    .retrieve()
                    .bodyToFlux(FinnhubNewsDTO.class)
                    .collectList()
                    .block();

            if (response == null) return Collections.emptyList();

            String firstWord = companyName != null ? companyName.toLowerCase().split(" ")[0] : "";

            return response.stream()
                    .filter(dto -> {
                        String headline = dto.getHeadline();
                        if (headline == null) return false;
                        String h = headline.toLowerCase();
                        return h.contains(ticker.toLowerCase()) ||
                                (!firstWord.isEmpty() && h.contains(firstWord));
                    })
                    .map(dto -> {
                        NewsArticle article = new NewsArticle();
                        article.setTicker(ticker);
                        article.setHeadline(dto.getHeadline());
                        article.setSummary(dto.getSummary());
                        article.setUrl(dto.getUrl());
                        article.setSource(dto.getSource());
                        article.setPublishedAt(LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(dto.getPublishedTimestamp()),
                                ZoneId.systemDefault()));
                        article.setSentiment(NewsArticle.Sentiment.NEUTRAL);
                        return article;
                    }).toList();

        } catch (Exception e) {
            System.err.println("Finnhub API error for " + ticker + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }
    public void updateStockPrice(String ticker) {
        try {
            Map response = webClient.get()
                    .uri(uri -> uri.path("/quote")
                            .queryParam("symbol", ticker)
                            .queryParam("token", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) return;

            Object price = response.get("c");
            if (price == null || price.toString().equals("0.0")) return;

            stockRepository.findByTicker(ticker).ifPresent(stock -> {
                stock.setCurrentPrice(new java.math.BigDecimal(price.toString()));
                stock.setLastUpdated(java.time.LocalDateTime.now());
                stockRepository.save(stock);
            });
        } catch (Exception e) {
            System.err.println("Finnhub quote error for " + ticker + ": " + e.getMessage());
        }
    }
}