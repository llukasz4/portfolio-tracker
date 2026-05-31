package com.tracker.portfolio_tracker.service;

import com.tracker.portfolio_tracker.model.Position;
import com.tracker.portfolio_tracker.model.Stock;
import com.tracker.portfolio_tracker.repository.PositionRepository;
import com.tracker.portfolio_tracker.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final StockRepository stockRepository;
    private final PositionRepository positionRepository;

    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    public Position addPosition(Position position) {
        return positionRepository.save(position);
    }

    public void deletePosition(Long id) {
        positionRepository.deleteById(id);
    }

    public BigDecimal getTotalValue() {
        return positionRepository.findAll()
                .stream()
                .filter(p -> p.getStock().getCurrentPrice() != null)
                .map(p -> p.getStock().getCurrentPrice().multiply(p.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalProfitLoss() {
        return positionRepository.findAll()
                .stream()
                .filter(p -> p.getStock().getCurrentPrice() != null)
                .map(Position::getProfitLoss)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}