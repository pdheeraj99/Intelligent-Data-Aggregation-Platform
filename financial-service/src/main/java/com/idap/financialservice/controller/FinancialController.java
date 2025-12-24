package com.idap.financialservice.controller;

import com.idap.financialservice.dto.StockQuoteResponse;
import com.idap.financialservice.dto.CryptoQuoteResponse;
import com.idap.financialservice.service.FinancialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for financial data endpoints.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class FinancialController {

    private final FinancialService financialService;

    /**
     * Get current stock quote.
     * GET /api/stocks/{symbol}
     */
    @GetMapping("/stocks/{symbol}")
    public ResponseEntity<StockQuoteResponse> getStockQuote(@PathVariable String symbol) {
        log.info("Request for stock quote: {}", symbol);
        return ResponseEntity.ok(financialService.getStockQuote(symbol));
    }

    /**
     * Get stock price history.
     * GET /api/stocks/{symbol}/history?hours=24
     */
    @GetMapping("/stocks/{symbol}/history")
    public ResponseEntity<List<StockQuoteResponse>> getStockHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "24") int hours) {
        log.info("Request for stock history: {} (last {} hours)", symbol, hours);
        return ResponseEntity.ok(financialService.getStockHistory(symbol, hours));
    }

    /**
     * Get current crypto quote.
     * GET /api/crypto/{symbol}
     */
    @GetMapping("/crypto/{symbol}")
    public ResponseEntity<CryptoQuoteResponse> getCryptoQuote(@PathVariable String symbol) {
        log.info("Request for crypto quote: {}", symbol);
        return ResponseEntity.ok(financialService.getCryptoQuote(symbol));
    }

    /**
     * Get crypto price history.
     * GET /api/crypto/{symbol}/history?hours=24
     */
    @GetMapping("/crypto/{symbol}/history")
    public ResponseEntity<List<CryptoQuoteResponse>> getCryptoHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "24") int hours) {
        log.info("Request for crypto history: {} (last {} hours)", symbol, hours);
        return ResponseEntity.ok(financialService.getCryptoHistory(symbol, hours));
    }
}
