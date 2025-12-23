package com.idap.weatherservice.controller;

import com.idap.weatherservice.dto.ForecastResponse;
import com.idap.weatherservice.dto.WeatherResponse;
import com.idap.weatherservice.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for weather endpoints.
 */
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * Get current weather for a city.
     * GET /api/weather/current?city={city}
     */
    @GetMapping("/current")
    public ResponseEntity<WeatherResponse> getCurrentWeather(
            @RequestParam String city,
            @RequestHeader(value = "X-User-Username", required = false) String username) {
        
        log.info("Current weather request for city: {} by user: {}", city, username);
        WeatherResponse response = weatherService.getCurrentWeather(city);
        return ResponseEntity.ok(response);
    }

    /**
     * Get weather forecast for a city.
     * GET /api/weather/forecast?city={city}&days={days}
     */
    @GetMapping("/forecast")
    public ResponseEntity<ForecastResponse> getForecast(
            @RequestParam String city,
            @RequestParam(defaultValue = "5") int days,
            @RequestHeader(value = "X-User-Username", required = false) String username) {
        
        log.info("Forecast request for city: {}, days: {} by user: {}", city, days, username);
        
        // Limit days to 1-5 (OpenWeatherMap free tier limit)
        days = Math.max(1, Math.min(5, days));
        
        ForecastResponse response = weatherService.getForecast(city, days);
        return ResponseEntity.ok(response);
    }

    /**
     * Get historical weather data for a city.
     * GET /api/weather/history?city={city}&hours={hours}
     */
    @GetMapping("/history")
    public ResponseEntity<List<WeatherResponse>> getHistoricalData(
            @RequestParam String city,
            @RequestParam(defaultValue = "24") int hours,
            @RequestHeader(value = "X-User-Username", required = false) String username) {
        
        log.info("Historical data request for city: {}, hours: {} by user: {}", city, hours, username);
        
        // Limit hours to prevent huge queries
        hours = Math.max(1, Math.min(168, hours)); // Max 7 days
        
        List<WeatherResponse> data = weatherService.getHistoricalData(city, hours);
        return ResponseEntity.ok(data);
    }

    /**
     * Get last known weather for a city (from database).
     * GET /api/weather/last?city={city}
     */
    @GetMapping("/last")
    public ResponseEntity<WeatherResponse> getLastKnownWeather(@RequestParam String city) {
        log.info("Last known weather request for city: {}", city);
        return weatherService.getLastKnownWeather(city)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

}
