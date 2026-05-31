package com.tracker.portfolio_tracker.service;

import com.tracker.portfolio_tracker.model.PriceAlert;
import com.tracker.portfolio_tracker.model.Stock;
import com.tracker.portfolio_tracker.repository.PriceAlertRepository;
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
class AlertServiceTest {

    @Mock
    private PriceAlertRepository priceAlertRepository;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private AlertService alertService;

    private Stock stock;
    private PriceAlert alertAbove;
    private PriceAlert alertBelow;

    @BeforeEach
    void setUp() {
        stock = new Stock();
        stock.setId(1L);
        stock.setTicker("AAPL");
        stock.setCurrentPrice(new BigDecimal("200.00"));

        alertAbove = new PriceAlert();
        alertAbove.setId(1L);
        alertAbove.setStock(stock);
        alertAbove.setTargetPrice(new BigDecimal("180.00"));
        alertAbove.setType(PriceAlert.AlertType.ABOVE);
        alertAbove.setTriggered(false);

        alertBelow = new PriceAlert();
        alertBelow.setId(2L);
        alertBelow.setStock(stock);
        alertBelow.setTargetPrice(new BigDecimal("220.00"));
        alertBelow.setType(PriceAlert.AlertType.BELOW);
        alertBelow.setTriggered(false);
    }

    @Test
    void shouldTriggerAboveAlert() {
        when(priceAlertRepository.findByTriggeredFalse()).thenReturn(List.of(alertAbove));

        alertService.checkAlerts();

        assertTrue(alertAbove.isTriggered());
        verify(priceAlertRepository, times(1)).save(alertAbove);
    }

    @Test
    void shouldTriggerBelowAlert() {
        when(priceAlertRepository.findByTriggeredFalse()).thenReturn(List.of(alertBelow));

        alertService.checkAlerts();

        assertTrue(alertBelow.isTriggered());
        verify(priceAlertRepository, times(1)).save(alertBelow);
    }

    @Test
    void shouldNotTriggerAlertWhenConditionNotMet() {
        stock.setCurrentPrice(new BigDecimal("170.00"));
        when(priceAlertRepository.findByTriggeredFalse()).thenReturn(List.of(alertAbove));

        alertService.checkAlerts();

        assertFalse(alertAbove.isTriggered());
        verify(priceAlertRepository, never()).save(alertAbove);
    }

    @Test
    void shouldSaveAlert() {
        when(priceAlertRepository.save(alertAbove)).thenReturn(alertAbove);

        PriceAlert result = alertService.addAlert(alertAbove);

        assertNotNull(result);
        verify(priceAlertRepository, times(1)).save(alertAbove);
    }

    @Test
    void shouldDeleteAlert() {
        alertService.deleteAlert(1L);
        verify(priceAlertRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldReturnActiveAlerts() {
        when(priceAlertRepository.findByTriggeredFalse()).thenReturn(List.of(alertAbove, alertBelow));

        List<PriceAlert> result = alertService.getActiveAlerts();

        assertEquals(2, result.size());
    }
}