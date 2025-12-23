package com.idap.weatherservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Response DTO for current weather data.
 * This DTO is also cached in Redis, so it implements Serializable.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse implements Serializable {

    private String city;
    private String country;
    private Double temperature;
    private Double feelsLike;
    private Integer humidity;
    private Integer pressure;
    private Double windSpeed;
    private String windDirection;
    private String weatherMain;
    private String weatherDescription;
    private String weatherIcon;
    private Integer cloudiness;
    private Integer visibility;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private String source; // "API" or "CACHE" or "FALLBACK"

}
