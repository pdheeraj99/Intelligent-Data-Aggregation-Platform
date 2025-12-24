package com.idap.financialservice.repository;

import com.idap.financialservice.model.CryptoQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoQuoteRepository extends JpaRepository<CryptoQuote, Long> {

    Optional<CryptoQuote> findTopBySymbolOrderByTimestampDesc(String symbol);

    List<CryptoQuote> findBySymbolOrderByTimestampDesc(String symbol);

    @Query("SELECT c FROM CryptoQuote c WHERE c.symbol = :symbol AND c.timestamp >= :since ORDER BY c.timestamp DESC")
    List<CryptoQuote> findBySymbolSince(String symbol, LocalDateTime since);

    @Query("SELECT DISTINCT c.symbol FROM CryptoQuote c")
    List<String> findAllSymbols();
}
