package com.tracker.portfolio_tracker.repository;

import com.tracker.portfolio_tracker.model.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {
    List<PriceAlert> findByStockTickerAndTriggeredFalse(String ticker);
    List<PriceAlert> findByTriggeredFalse();
}