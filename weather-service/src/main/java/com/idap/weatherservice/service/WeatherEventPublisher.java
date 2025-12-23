package com.idap.weatherservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idap.weatherservice.dto.WeatherEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service for publishing weather events to Kafka.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.weather-updated:weather.updated}")
    private String weatherUpdatedTopic;

    /**
     * Publish weather update event to Kafka.
     * Events are published when weather data is fetched/updated.
     */
    public void publishWeatherUpdate(WeatherEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            String key = event.getCity().toLowerCase().replace(" ", "-");

            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(weatherUpdatedTopic, key, message);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish weather event for city {}: {}", 
                        event.getCity(), ex.getMessage());
                } else {
                    log.info("Published weather event for city {} to partition {} at offset {}",
                        event.getCity(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                }
            });

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize weather event: {}", e.getMessage());
        }
    }

    /**
     * Publish temperature alert when temperature changes by more than 5°C.
     */
    public void publishTemperatureAlert(WeatherEvent event) {
        if (Math.abs(event.getTemperatureChange()) > 5.0) {
            log.warn("TEMPERATURE ALERT: {} changed by {}°C", 
                event.getCity(), event.getTemperatureChange());
            publishWeatherUpdate(event);
        }
    }

}
