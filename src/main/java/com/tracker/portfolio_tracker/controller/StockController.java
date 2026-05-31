package com.tracker.portfolio_tracker.controller;

import com.tracker.portfolio_tracker.api.FinnhubClient;
import com.tracker.portfolio_tracker.api.StooqClient;
import com.tracker.portfolio_tracker.model.Stock;
import com.tracker.portfolio_tracker.repository.StockRepository;
import com.tracker.portfolio_tracker.service.FinnhubWebSocketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockRepository stockRepository;
    private final FinnhubWebSocketService webSocketService;
    private final FinnhubClient finnhubClient;
    private final StooqClient stooqClient;

    private boolean isPolishStock(String ticker) {
        return ticker.endsWith(".PL") || ticker.endsWith(".WA");
    }

    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        return ResponseEntity.ok(stockRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Stock> addStock(@Valid @RequestBody Stock stock) {
        Stock saved = stockRepository.findByTicker(stock.getTicker())
                .orElseGet(() -> stockRepository.save(stock));

        if (isPolishStock(saved.getTicker())) {
            stooqClient.updateStockPrice(saved.getTicker());
        } else {
            webSocketService.subscribeToNewTicker(saved.getTicker());
            finnhubClient.updateStockPrice(saved.getTicker());
        }

        return ResponseEntity.ok(stockRepository.findByTicker(saved.getTicker()).orElse(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        stockRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}