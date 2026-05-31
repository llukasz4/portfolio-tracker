package com.tracker.portfolio_tracker.scheduler;

import com.tracker.portfolio_tracker.api.FinnhubClient;
import com.tracker.portfolio_tracker.api.StooqClient;
import com.tracker.portfolio_tracker.model.NewsArticle;
import com.tracker.portfolio_tracker.repository.StockRepository;
import com.tracker.portfolio_tracker.service.AlertService;
import com.tracker.portfolio_tracker.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsScheduler {

    private final StockRepository stockRepository;
    private final FinnhubClient finnhubClient;
    private final StooqClient stooqClient;
    private final NewsService newsService;
    private final AlertService alertService;

    private boolean isPolishStock(String ticker) {
        return ticker.endsWith(".PL") || ticker.endsWith(".WA");
    }

    @Scheduled(fixedRateString = "${news.refresh.rate}")
    public void fetchNewsAndUpdatePrices() {
        List<com.tracker.portfolio_tracker.model.Stock> stocks = stockRepository.findAll();

        for (com.tracker.portfolio_tracker.model.Stock stock : stocks) {
            if (isPolishStock(stock.getTicker())) {
                stooqClient.updateStockPrice(stock.getTicker());
            } else {
                finnhubClient.updateStockPrice(stock.getTicker());
                List<NewsArticle> articles = finnhubClient.getNewsForTicker(stock.getTicker(), stock.getCompanyName());
                articles.forEach(newsService::saveIfNotExists);
                System.out.println("Updated: " + stock.getTicker() + " | News: " + articles.size());
            }
        }

        alertService.checkAlerts();
    }
}