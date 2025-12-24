package com.idap.financialservice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for stock quote response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockQuoteResponse {
    private String symbol;
    private String companyName;
    private BigDecimal currentPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal openPrice;
    private BigDecimal previousClose;
    private BigDecimal change;
    private BigDecimal percentChange;
    private Long volume;
    private String exchange;
    private LocalDateTime timestamp;
    private String source;
}
