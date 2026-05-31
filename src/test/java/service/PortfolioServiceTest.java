package com.tracker.portfolio_tracker.service;

import com.tracker.portfolio_tracker.model.Position;
import com.tracker.portfolio_tracker.model.Stock;
import com.tracker.portfolio_tracker.repository.PositionRepository;
import com.tracker.portfolio_tracker.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    private Stock stock;
    private Position position;

    @BeforeEach
    void setUp() {
        stock = new Stock();
        stock.setId(1L);
        stock.setTicker("AAPL");
        stock.setCompanyName("Apple Inc.");
        stock.setCurrentPrice(new BigDecimal("200.00"));

        position = new Position();
        position.setId(1L);
        position.setStock(stock);
        position.setQuantity(new BigDecimal("10"));
        position.setAvgBuyPrice(new BigDecimal("150.00"));
    }

    @Test
    void shouldReturnAllPositions() {
        when(positionRepository.findAll()).thenReturn(List.of(position));

        List<Position> result = portfolioService.getAllPositions();

        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getStock().getTicker());
        verify(positionRepository, times(1)).findAll();
    }

    @Test
    void shouldCalculateTotalValue() {
        when(positionRepository.findAll()).thenReturn(List.of(position));

        BigDecimal result = portfolioService.getTotalValue();

        assertEquals(new BigDecimal("2000.00"), result);
    }

    @Test
    void shouldCalculateTotalProfitLoss() {
        when(positionRepository.findAll()).thenReturn(List.of(position));

        BigDecimal result = portfolioService.getTotalProfitLoss();

        assertEquals(new BigDecimal("500.00"), result);
    }

    @Test
    void shouldSavePosition() {
        when(positionRepository.save(position)).thenReturn(position);

        Position result = portfolioService.addPosition(position);

        assertNotNull(result);
        verify(positionRepository, times(1)).save(position);
    }

    @Test
    void shouldDeletePosition() {
        portfolioService.deletePosition(1L);
        verify(positionRepository, times(1)).deleteById(1L);
    }
}