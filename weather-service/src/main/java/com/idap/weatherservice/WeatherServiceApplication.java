package com.idap.weatherservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Weather Data Service for IDAP.
 * Fetches weather data from OpenWeatherMap API, caches in Redis,
 * stores historical data in PostgreSQL, and publishes events to Kafka.
 * 
 * Endpoints:
 * - GET /api/weather/current?city={city} - Get current weather
 * - GET /api/weather/forecast?city={city}&days={days} - Get weather forecast
 * - GET /api/weather/history?city={city} - Get historical weather data
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@EnableScheduling
public class WeatherServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherServiceApplication.class, args);
    }

}
