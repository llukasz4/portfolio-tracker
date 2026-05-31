package com.tracker.portfolio_tracker.controller;

import com.tracker.portfolio_tracker.model.Position;
import com.tracker.portfolio_tracker.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/positions")
    public ResponseEntity<List<Position>> getAllPositions() {
        return ResponseEntity.ok(portfolioService.getAllPositions());
    }

    @PostMapping("/positions")
    public ResponseEntity<Position> addPosition(@Valid @RequestBody Position position) {
        return ResponseEntity.ok(portfolioService.addPosition(position));
    }

    @DeleteMapping("/positions/{id}")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        portfolioService.deletePosition(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/value")
    public ResponseEntity<BigDecimal> getTotalValue() {
        return ResponseEntity.ok(portfolioService.getTotalValue());
    }

    @GetMapping("/pnl")
    public ResponseEntity<BigDecimal> getTotalProfitLoss() {
        return ResponseEntity.ok(portfolioService.getTotalProfitLoss());
    }
}