package com.tracker.portfolio_tracker.service;

import com.tracker.portfolio_tracker.model.PriceAlert;
import com.tracker.portfolio_tracker.repository.PriceAlertRepository;
import com.tracker.portfolio_tracker.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final PriceAlertRepository priceAlertRepository;
    private final StockRepository stockRepository;

    public PriceAlert addAlert(PriceAlert alert) {
        return priceAlertRepository.save(alert);
    }

    public void deleteAlert(Long id) {
        priceAlertRepository.deleteById(id);
    }

    public List<PriceAlert> getActiveAlerts() {
        return priceAlertRepository.findByTriggeredFalse();
    }

    public void checkAlerts() {
        priceAlertRepository.findByTriggeredFalse().forEach(alert -> {
            var currentPrice = alert.getStock().getCurrentPrice();
            boolean triggered = switch (alert.getType()) {
                case ABOVE -> currentPrice.compareTo(alert.getTargetPrice()) >= 0;
                case BELOW -> currentPrice.compareTo(alert.getTargetPrice()) <= 0;
            };
            if (triggered) {
                alert.setTriggered(true);
                priceAlertRepository.save(alert);
                System.out.println("Alert triggered: " + alert.getStock().getTicker()
                        + " @ " + currentPrice);
            }
        });
    }
}