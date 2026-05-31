package com.tracker.portfolio_tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "positions")
@Getter @Setter @NoArgsConstructor
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @NotNull
    @Positive
    private BigDecimal quantity;

    @NotNull
    @Positive
    private BigDecimal avgBuyPrice;

    private LocalDateTime openedAt;

    @Transient
    public BigDecimal getProfitLoss() {
        if (stock.getCurrentPrice() == null) return BigDecimal.ZERO;
        return stock.getCurrentPrice()
                .subtract(avgBuyPrice)
                .multiply(quantity);
    }
}