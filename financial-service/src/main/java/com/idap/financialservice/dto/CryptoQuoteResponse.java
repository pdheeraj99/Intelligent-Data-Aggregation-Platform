package com.idap.financialservice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for cryptocurrency quote response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CryptoQuoteResponse {
    private String symbol;
    private String baseCurrency;
    private String quoteCurrency;
    private BigDecimal price;
    private BigDecimal high24h;
    private BigDecimal low24h;
    private BigDecimal percentChange24h;
    private BigDecimal volume24h;
    private BigDecimal marketCap;
    private LocalDateTime timestamp;
    private String source;
}
