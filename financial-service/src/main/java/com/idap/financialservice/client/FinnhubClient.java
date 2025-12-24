package com.idap.financialservice.client;

import com.idap.financialservice.dto.StockQuoteResponse;
import com.idap.financialservice.dto.CryptoQuoteResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

/**
 * Client for Finnhub API - provides stock and crypto data.
 * Includes circuit breaker and mock fallback for development.
 */
@Component
@Slf4j
public class FinnhubClient {

    private final WebClient webClient;
    private final String apiKey;
    private final Random random = new Random();

    public FinnhubClient(
            WebClient.Builder webClientBuilder,
            @Value("${finnhub.api.key:demo}") String apiKey,
            @Value("${finnhub.api.base-url:https://finnhub.io/api/v1}") String baseUrl) {
        this.apiKey = apiKey;
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    @CircuitBreaker(name = "finnhub", fallbackMethod = "getStockQuoteFallback")
    public StockQuoteResponse getStockQuote(String symbol) {
        if ("demo".equals(apiKey)) {
            log.info("Using mock data for stock {} (API key not configured)", symbol);
            return generateMockStockQuote(symbol);
        }

        log.info("Fetching stock quote for {} from Finnhub", symbol);
        try {
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/quote")
                            .queryParam("symbol", symbol)
                            .queryParam("token", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || response.isEmpty()) {
                return generateMockStockQuote(symbol);
            }

            return StockQuoteResponse.builder()
                    .symbol(symbol)
                    .currentPrice(getBigDecimal(response.get("c")))
                    .highPrice(getBigDecimal(response.get("h")))
                    .lowPrice(getBigDecimal(response.get("l")))
                    .openPrice(getBigDecimal(response.get("o")))
                    .previousClose(getBigDecimal(response.get("pc")))
                    .change(getBigDecimal(response.get("d")))
                    .percentChange(getBigDecimal(response.get("dp")))
                    .timestamp(LocalDateTime.now())
                    .source("FINNHUB")
                    .build();
        } catch (Exception e) {
            log.error("Error fetching stock quote: {}", e.getMessage());
            return generateMockStockQuote(symbol);
        }
    }

    public StockQuoteResponse getStockQuoteFallback(String symbol, Throwable t) {
        log.warn("Finnhub circuit breaker open for stock {}: {}", symbol, t.getMessage());
        return generateMockStockQuote(symbol);
    }

    @CircuitBreaker(name = "finnhub", fallbackMethod = "getCryptoQuoteFallback")
    public CryptoQuoteResponse getCryptoQuote(String symbol) {
        if ("demo".equals(apiKey)) {
            log.info("Using mock data for crypto {} (API key not configured)", symbol);
            return generateMockCryptoQuote(symbol);
        }

        log.info("Fetching crypto quote for {} from Finnhub", symbol);
        try {
            // Finnhub uses BINANCE:BTCUSDT format for crypto
            String exchange = "BINANCE";
            String finnhubSymbol = exchange + ":" + symbol;
            
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/quote")
                            .queryParam("symbol", finnhubSymbol)
                            .queryParam("token", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || response.isEmpty()) {
                return generateMockCryptoQuote(symbol);
            }

            String base = symbol.length() > 4 ? symbol.substring(0, symbol.length() - 4) : symbol;
            String quote = symbol.length() > 4 ? symbol.substring(symbol.length() - 4) : "USDT";

            return CryptoQuoteResponse.builder()
                    .symbol(symbol)
                    .baseCurrency(base)
                    .quoteCurrency(quote)
                    .price(getBigDecimal(response.get("c")))
                    .high24h(getBigDecimal(response.get("h")))
                    .low24h(getBigDecimal(response.get("l")))
                    .percentChange24h(getBigDecimal(response.get("dp")))
                    .timestamp(LocalDateTime.now())
                    .source("FINNHUB")
                    .build();
        } catch (Exception e) {
            log.error("Error fetching crypto quote: {}", e.getMessage());
            return generateMockCryptoQuote(symbol);
        }
    }

    public CryptoQuoteResponse getCryptoQuoteFallback(String symbol, Throwable t) {
        log.warn("Finnhub circuit breaker open for crypto {}: {}", symbol, t.getMessage());
        return generateMockCryptoQuote(symbol);
    }

    private StockQuoteResponse generateMockStockQuote(String symbol) {
        BigDecimal basePrice = getBaseMockPrice(symbol);
        BigDecimal change = BigDecimal.valueOf((random.nextDouble() - 0.5) * 10)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal percentChange = change.divide(basePrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return StockQuoteResponse.builder()
                .symbol(symbol)
                .companyName(getMockCompanyName(symbol))
                .currentPrice(basePrice.add(change))
                .highPrice(basePrice.add(BigDecimal.valueOf(5)))
                .lowPrice(basePrice.subtract(BigDecimal.valueOf(3)))
                .openPrice(basePrice)
                .previousClose(basePrice)
                .change(change)
                .percentChange(percentChange.setScale(2, RoundingMode.HALF_UP))
                .volume(random.nextLong(1000000, 50000000))
                .exchange("NASDAQ")
                .timestamp(LocalDateTime.now())
                .source("MOCK")
                .build();
    }

    private CryptoQuoteResponse generateMockCryptoQuote(String symbol) {
        BigDecimal basePrice = getMockCryptoPrice(symbol);
        BigDecimal percentChange = BigDecimal.valueOf((random.nextDouble() - 0.5) * 10)
                .setScale(2, RoundingMode.HALF_UP);

        String base = symbol.replace("USDT", "").replace("USD", "");
        
        return CryptoQuoteResponse.builder()
                .symbol(symbol)
                .baseCurrency(base)
                .quoteCurrency("USD")
                .price(basePrice)
                .high24h(basePrice.multiply(BigDecimal.valueOf(1.05)))
                .low24h(basePrice.multiply(BigDecimal.valueOf(0.95)))
                .percentChange24h(percentChange)
                .volume24h(BigDecimal.valueOf(random.nextDouble() * 1000000000))
                .marketCap(basePrice.multiply(BigDecimal.valueOf(1000000000)))
                .timestamp(LocalDateTime.now())
                .source("MOCK")
                .build();
    }

    private BigDecimal getBaseMockPrice(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "AAPL" -> BigDecimal.valueOf(175.50);
            case "GOOGL" -> BigDecimal.valueOf(140.25);
            case "MSFT" -> BigDecimal.valueOf(378.90);
            case "AMZN" -> BigDecimal.valueOf(153.75);
            case "TSLA" -> BigDecimal.valueOf(248.50);
            case "META" -> BigDecimal.valueOf(325.40);
            case "NVDA" -> BigDecimal.valueOf(485.60);
            default -> BigDecimal.valueOf(100 + random.nextDouble() * 200);
        };
    }

    private BigDecimal getMockCryptoPrice(String symbol) {
        String base = symbol.replace("USDT", "").replace("USD", "").toUpperCase();
        return switch (base) {
            case "BTC" -> BigDecimal.valueOf(43250.75);
            case "ETH" -> BigDecimal.valueOf(2285.50);
            case "SOL" -> BigDecimal.valueOf(98.45);
            case "XRP" -> BigDecimal.valueOf(0.62);
            case "ADA" -> BigDecimal.valueOf(0.58);
            case "DOGE" -> BigDecimal.valueOf(0.092);
            default -> BigDecimal.valueOf(random.nextDouble() * 100);
        };
    }

    private String getMockCompanyName(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "AAPL" -> "Apple Inc.";
            case "GOOGL" -> "Alphabet Inc.";
            case "MSFT" -> "Microsoft Corporation";
            case "AMZN" -> "Amazon.com Inc.";
            case "TSLA" -> "Tesla Inc.";
            case "META" -> "Meta Platforms Inc.";
            case "NVDA" -> "NVIDIA Corporation";
            default -> symbol + " Inc.";
        };
    }

    private BigDecimal getBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        try {
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
