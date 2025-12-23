package com.idap.weatherservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for weather forecast.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastResponse implements Serializable {

    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    private List<ForecastDay> forecast;
    private String source;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ForecastDay implements Serializable {
        private LocalDateTime dateTime;
        private Double temperature;
        private Double temperatureMin;
        private Double temperatureMax;
        private Double feelsLike;
        private Integer humidity;
        private Integer pressure;
        private Double windSpeed;
        private String weatherMain;
        private String weatherDescription;
        private String weatherIcon;
        private Integer cloudiness;
        private Double probabilityOfPrecipitation;
    }

}
