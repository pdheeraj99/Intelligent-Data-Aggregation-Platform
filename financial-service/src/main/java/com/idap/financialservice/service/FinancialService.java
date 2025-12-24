package com.idap.financialservice.service;

import com.idap.financialservice.client.FinnhubClient;
import com.idap.financialservice.dto.StockQuoteResponse;
import com.idap.financialservice.dto.CryptoQuoteResponse;
import com.idap.financialservice.model.StockQuote;
import com.idap.financialservice.model.CryptoQuote;
import com.idap.financialservice.repository.StockQuoteRepository;
import com.idap.financialservice.repository.CryptoQuoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for financial data operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialService {

    private final FinnhubClient finnhubClient;
    private final StockQuoteRepository stockQuoteRepository;
    private final CryptoQuoteRepository cryptoQuoteRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String STOCK_TOPIC = "financial.stock.updated";
    private static final String CRYPTO_TOPIC = "financial.crypto.updated";

    @Cacheable(value = "stockQuotes", key = "#symbol", unless = "#result.source == 'MOCK'")
    public StockQuoteResponse getStockQuote(String symbol) {
        log.info("Getting stock quote for {}", symbol);
        StockQuoteResponse response = finnhubClient.getStockQuote(symbol.toUpperCase());

        // Save to database
        StockQuote entity = StockQuote.builder()
                .symbol(response.getSymbol())
                .companyName(response.getCompanyName())
                .currentPrice(response.getCurrentPrice())
                .highPrice(response.getHighPrice())
                .lowPrice(response.getLowPrice())
                .openPrice(response.getOpenPrice())
                .previousClose(response.getPreviousClose())
                .change(response.getChange())
                .percentChange(response.getPercentChange())
                .volume(response.getVolume())
                .exchange(response.getExchange())
                .timestamp(LocalDateTime.now())
                .build();
        stockQuoteRepository.save(entity);

        // Publish event
        publishStockEvent(response);

        return response;
    }

    @Cacheable(value = "cryptoQuotes", key = "#symbol", unless = "#result.source == 'MOCK'")
    public CryptoQuoteResponse getCryptoQuote(String symbol) {
        log.info("Getting crypto quote for {}", symbol);
        CryptoQuoteResponse response = finnhubClient.getCryptoQuote(symbol.toUpperCase());

        // Save to database
        CryptoQuote entity = CryptoQuote.builder()
                .symbol(response.getSymbol())
                .baseCurrency(response.getBaseCurrency())
                .quoteCurrency(response.getQuoteCurrency())
                .price(response.getPrice())
                .high24h(response.getHigh24h())
                .low24h(response.getLow24h())
                .percentChange24h(response.getPercentChange24h())
                .volume24h(response.getVolume24h())
                .marketCap(response.getMarketCap())
                .timestamp(LocalDateTime.now())
                .build();
        cryptoQuoteRepository.save(entity);

        // Publish event
        publishCryptoEvent(response);

        return response;
    }

    public List<StockQuoteResponse> getStockHistory(String symbol, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return stockQuoteRepository.findBySymbolSince(symbol.toUpperCase(), since).stream()
                .map(this::mapToStockResponse)
                .toList();
    }

    public List<CryptoQuoteResponse> getCryptoHistory(String symbol, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return cryptoQuoteRepository.findBySymbolSince(symbol.toUpperCase(), since).stream()
                .map(this::mapToCryptoResponse)
                .toList();
    }

    private void publishStockEvent(StockQuoteResponse quote) {
        try {
            kafkaTemplate.send(STOCK_TOPIC, quote.getSymbol(), quote);
            log.debug("Published stock event for {}", quote.getSymbol());
        } catch (Exception e) {
            log.error("Failed to publish stock event: {}", e.getMessage());
        }
    }

    private void publishCryptoEvent(CryptoQuoteResponse quote) {
        try {
            kafkaTemplate.send(CRYPTO_TOPIC, quote.getSymbol(), quote);
            log.debug("Published crypto event for {}", quote.getSymbol());
        } catch (Exception e) {
            log.error("Failed to publish crypto event: {}", e.getMessage());
        }
    }

    private StockQuoteResponse mapToStockResponse(StockQuote entity) {
        return StockQuoteResponse.builder()
                .symbol(entity.getSymbol())
                .companyName(entity.getCompanyName())
                .currentPrice(entity.getCurrentPrice())
                .highPrice(entity.getHighPrice())
                .lowPrice(entity.getLowPrice())
                .openPrice(entity.getOpenPrice())
                .previousClose(entity.getPreviousClose())
                .change(entity.getChange())
                .percentChange(entity.getPercentChange())
                .volume(entity.getVolume())
                .exchange(entity.getExchange())
                .timestamp(entity.getTimestamp())
                .source("DATABASE")
                .build();
    }

    private CryptoQuoteResponse mapToCryptoResponse(CryptoQuote entity) {
        return CryptoQuoteResponse.builder()
                .symbol(entity.getSymbol())
                .baseCurrency(entity.getBaseCurrency())
                .quoteCurrency(entity.getQuoteCurrency())
                .price(entity.getPrice())
                .high24h(entity.getHigh24h())
                .low24h(entity.getLow24h())
                .percentChange24h(entity.getPercentChange24h())
                .volume24h(entity.getVolume24h())
                .marketCap(entity.getMarketCap())
                .timestamp(entity.getTimestamp())
                .source("DATABASE")
                .build();
    }
}
