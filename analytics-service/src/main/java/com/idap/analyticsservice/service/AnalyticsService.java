package com.idap.analyticsservice.service;

import com.idap.analyticsservice.model.Insight;
import com.idap.analyticsservice.repository.InsightRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final InsightRepository insightRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void processWeatherEvent(Map<String, Object> event) {
        try {
            String city = (String) event.get("city");
            Double temp = getDouble(event.get("temperature"));
            
            // Generate weather insight
            if (temp != null && temp > 30) {
                saveInsight("WEATHER_ALERT", 
                    "High Temperature Alert: " + city,
                    "Temperature in " + city + " has reached " + temp + "°C",
                    "weather", 0.8, event);
            } else if (temp != null && temp < 0) {
                saveInsight("WEATHER_ALERT",
                    "Freezing Temperature Alert: " + city,
                    "Temperature in " + city + " has dropped to " + temp + "°C",
                    "weather", 0.8, event);
            }
        } catch (Exception e) {
            log.error("Error processing weather event: {}", e.getMessage());
        }
    }

    public void processStockEvent(Map<String, Object> event) {
        try {
            String symbol = (String) event.get("symbol");
            Double percentChange = getDouble(event.get("percentChange"));
            
            // Generate stock insight for significant moves
            if (percentChange != null && Math.abs(percentChange) > 5) {
                String direction = percentChange > 0 ? "surged" : "dropped";
                saveInsight("STOCK_MOVEMENT",
                    symbol + " " + direction + " " + String.format("%.2f", Math.abs(percentChange)) + "%",
                    "Significant stock movement detected for " + symbol,
                    "stocks", Math.abs(percentChange) / 10, event);
            }
        } catch (Exception e) {
            log.error("Error processing stock event: {}", e.getMessage());
        }
    }

    public void processCryptoEvent(Map<String, Object> event) {
        try {
            String symbol = (String) event.get("symbol");
            Double percentChange = getDouble(event.get("percentChange24h"));
            
            // Generate crypto insight for significant moves
            if (percentChange != null && Math.abs(percentChange) > 3) {
                String direction = percentChange > 0 ? "up" : "down";
                saveInsight("CRYPTO_MOVEMENT",
                    symbol + " is " + direction + " " + String.format("%.2f", Math.abs(percentChange)) + "% in 24h",
                    "Crypto market movement detected for " + symbol,
                    "crypto", Math.abs(percentChange) / 10, event);
            }
        } catch (Exception e) {
            log.error("Error processing crypto event: {}", e.getMessage());
        }
    }

    public void processNewsEvent(Map<String, Object> event) {
        try {
            String title = (String) event.get("title");
            String category = (String) event.get("category");
            String sentiment = (String) event.get("sentiment");
            
            // Generate news insight
            if ("NEGATIVE".equals(sentiment)) {
                saveInsight("NEWS_SENTIMENT",
                    "Negative News: " + (title != null ? title.substring(0, Math.min(50, title.length())) : "Unknown"),
                    "Negative sentiment detected in " + category + " news",
                    category, 0.6, event);
            }
        } catch (Exception e) {
            log.error("Error processing news event: {}", e.getMessage());
        }
    }

    public List<Insight> getActiveInsights() {
        return insightRepository.findByActiveTrueOrderByGeneratedAtDesc();
    }

    public List<Insight> getTopInsights() {
        return insightRepository.findTop10ByActiveTrueOrderByScoreDesc();
    }

    public List<Insight> getInsightsByType(String type) {
        return insightRepository.findByTypeOrderByGeneratedAtDesc(type);
    }

    public List<Insight> getRecentInsights(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return insightRepository.findByGeneratedAtAfterOrderByGeneratedAtDesc(since);
    }

    private void saveInsight(String type, String title, String description, 
                            String category, Double score, Map<String, Object> data) {
        try {
            Insight insight = Insight.builder()
                    .type(type)
                    .title(title)
                    .description(description)
                    .category(category)
                    .score(score)
                    .data(objectMapper.writeValueAsString(data))
                    .generatedAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusHours(24))
                    .active(true)
                    .build();
            
            insightRepository.save(insight);
            log.info("Saved insight: {}", title);
        } catch (Exception e) {
            log.error("Error saving insight: {}", e.getMessage());
        }
    }

    private Double getDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
