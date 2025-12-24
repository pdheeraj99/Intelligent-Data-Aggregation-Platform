package com.idap.financialservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing stock price data.
 */
@Entity
@Table(name = "stock_quotes", indexes = {
    @Index(name = "idx_stock_symbol", columnList = "symbol"),
    @Index(name = "idx_stock_recorded_at", columnList = "recorded_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String symbol;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal currentPrice;

    @Column(precision = 12, scale = 4)
    private BigDecimal highPrice;

    @Column(precision = 12, scale = 4)
    private BigDecimal lowPrice;

    @Column(precision = 12, scale = 4)
    private BigDecimal openPrice;

    @Column(precision = 12, scale = 4)
    private BigDecimal previousClose;

    @Column(precision = 10, scale = 2)
    private BigDecimal percentChange;

    @Column(name = "price_change", precision = 12, scale = 4)
    private BigDecimal priceChange;

    private Long volume;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(length = 50)
    private String exchange;

    @Column(length = 100)
    private String companyName;

    @PrePersist
    protected void onCreate() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }
}
