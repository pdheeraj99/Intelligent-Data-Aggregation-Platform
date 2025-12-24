package com.idap.financialservice.repository;

import com.idap.financialservice.model.StockQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockQuoteRepository extends JpaRepository<StockQuote, Long> {

    Optional<StockQuote> findTopBySymbolOrderByTimestampDesc(String symbol);

    List<StockQuote> findBySymbolOrderByTimestampDesc(String symbol);

    @Query("SELECT s FROM StockQuote s WHERE s.symbol = :symbol AND s.timestamp >= :since ORDER BY s.timestamp DESC")
    List<StockQuote> findBySymbolSince(String symbol, LocalDateTime since);

    @Query("SELECT DISTINCT s.symbol FROM StockQuote s")
    List<String> findAllSymbols();
}
