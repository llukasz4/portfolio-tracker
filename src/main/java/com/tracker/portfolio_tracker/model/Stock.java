package com.tracker.portfolio_tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
@Getter @Setter @NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "No ticker, no trades")
    @Column(unique = true, nullable = false)
    private String ticker;

    @NotBlank(message = "No name, no company")
    private String companyName;

    private BigDecimal currentPrice;

    private LocalDateTime lastUpdated;
}