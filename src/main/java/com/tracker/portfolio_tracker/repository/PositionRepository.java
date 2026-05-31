package com.tracker.portfolio_tracker.repository;

import com.tracker.portfolio_tracker.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByStockTicker(String ticker);
}