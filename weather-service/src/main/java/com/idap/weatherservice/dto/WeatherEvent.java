package com.idap.weatherservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event DTO for Kafka weather.updated topic.
 * Published when temperature changes significantly (>5°C).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherEvent {

    private String eventType; // "WEATHER_UPDATED", "TEMPERATURE_ALERT"
    private String city;
    private String country;
    private Double previousTemperature;
    private Double currentTemperature;
    private Double temperatureChange;
    private String weatherMain;
    private String weatherDescription;
    private LocalDateTime timestamp;
    private String source;

    public static WeatherEvent fromWeatherResponse(WeatherResponse response, Double previousTemp) {
        Double change = previousTemp != null ? response.getTemperature() - previousTemp : 0.0;
        return WeatherEvent.builder()
            .eventType(Math.abs(change) > 5.0 ? "TEMPERATURE_ALERT" : "WEATHER_UPDATED")
            .city(response.getCity())
            .country(response.getCountry())
            .previousTemperature(previousTemp)
            .currentTemperature(response.getTemperature())
            .temperatureChange(change)
            .weatherMain(response.getWeatherMain())
            .weatherDescription(response.getWeatherDescription())
            .timestamp(response.getTimestamp())
            .source("weather-service")
            .build();
    }

}
