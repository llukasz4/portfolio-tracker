package com.tracker.portfolio_tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "price_alerts")
@Getter @Setter @NoArgsConstructor
public class PriceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @NotNull
    private BigDecimal targetPrice;

    @Enumerated(EnumType.STRING)
    private AlertType type;

    private boolean triggered = false;

    public enum AlertType {
        ABOVE, BELOW
    }
}