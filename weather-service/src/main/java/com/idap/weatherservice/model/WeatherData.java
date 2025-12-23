package com.idap.weatherservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity for storing historical weather data.
 * Each record represents weather data for a city at a specific time.
 */
@Entity
@Table(name = "weather_data", indexes = {
    @Index(name = "idx_city_timestamp", columnList = "city, timestamp DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double feelsLike;

    @Column(nullable = false)
    private Integer humidity;

    @Column(nullable = false)
    private Integer pressure;

    @Column(nullable = false)
    private Double windSpeed;

    @Column(length = 50)
    private String windDirection;

    @Column(nullable = false, length = 50)
    private String weatherMain;

    @Column(length = 200)
    private String weatherDescription;

    @Column(length = 10)
    private String weatherIcon;

    private Integer cloudiness;

    private Integer visibility;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Coordinates
    private Double latitude;
    private Double longitude;

}
