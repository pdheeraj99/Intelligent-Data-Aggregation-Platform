package com.idap.financialservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing cryptocurrency price data.
 */
@Entity
@Table(name = "crypto_quotes", indexes = {
    @Index(name = "idx_crypto_symbol", columnList = "symbol"),
    @Index(name = "idx_crypto_timestamp", columnList = "timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CryptoQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;  // e.g., BTCUSD, ETHUSD

    @Column(nullable = false, length = 20)
    private String baseCurrency;  // e.g., BTC, ETH

    @Column(nullable = false, length = 10)
    private String quoteCurrency;  // e.g., USD

    @Column(nullable = false, precision = 18, scale = 8)
    private BigDecimal price;

    @Column(precision = 18, scale = 8)
    private BigDecimal high24h;

    @Column(precision = 18, scale = 8)
    private BigDecimal low24h;

    @Column(precision = 10, scale = 4)
    private BigDecimal percentChange24h;

    @Column(precision = 24, scale = 2)
    private BigDecimal volume24h;

    @Column(precision = 24, scale = 2)
    private BigDecimal marketCap;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
