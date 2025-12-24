package com.idap.analyticsservice.consumer;

import com.idap.analyticsservice.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka consumer for all event topics.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = "weather.updated", groupId = "analytics-service")
    public void consumeWeatherEvent(Map<String, Object> event) {
        log.info("Received weather event: {}", event.get("city"));
        analyticsService.processWeatherEvent(event);
    }

    @KafkaListener(topics = "financial.stock.updated", groupId = "analytics-service")
    public void consumeStockEvent(Map<String, Object> event) {
        log.info("Received stock event: {}", event.get("symbol"));
        analyticsService.processStockEvent(event);
    }

    @KafkaListener(topics = "financial.crypto.updated", groupId = "analytics-service")
    public void consumeCryptoEvent(Map<String, Object> event) {
        log.info("Received crypto event: {}", event.get("symbol"));
        analyticsService.processCryptoEvent(event);
    }

    @KafkaListener(topics = "news.article.published", groupId = "analytics-service")
    public void consumeNewsEvent(Map<String, Object> event) {
        log.info("Received news event: {}", event.get("title"));
        analyticsService.processNewsEvent(event);
    }
}
