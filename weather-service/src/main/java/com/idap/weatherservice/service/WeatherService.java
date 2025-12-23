package com.idap.weatherservice.service;

import com.idap.weatherservice.client.OpenWeatherMapClient;
import com.idap.weatherservice.dto.ForecastResponse;
import com.idap.weatherservice.dto.WeatherEvent;
import com.idap.weatherservice.dto.WeatherResponse;
import com.idap.weatherservice.model.WeatherData;
import com.idap.weatherservice.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for weather data operations.
 * Implements caching, historical data storage, and event publishing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final OpenWeatherMapClient weatherClient;
    private final WeatherRepository weatherRepository;
    private final WeatherEventPublisher eventPublisher;

    /**
     * Get current weather for a city.
     * Results are cached in Redis for 10 minutes.
     */
    @Cacheable(value = "weather:current", key = "#city.toLowerCase()", unless = "#result.source == 'FALLBACK'")
    public WeatherResponse getCurrentWeather(String city) {
        log.info("Fetching current weather for city: {}", city);
        
        // Get previous temperature for comparison
        Optional<Double> previousTemp = getPreviousTemperature(city);
        
        // Fetch from API
        WeatherResponse response = weatherClient.getCurrentWeather(city);
        
        // Store in database
        saveWeatherData(response);
        
        // Publish event to Kafka
        WeatherEvent event = WeatherEvent.fromWeatherResponse(response, previousTemp.orElse(null));
        eventPublisher.publishWeatherUpdate(event);
        
        // Check for significant temperature change
        if (previousTemp.isPresent() && Math.abs(response.getTemperature() - previousTemp.get()) > 5.0) {
            eventPublisher.publishTemperatureAlert(event);
        }
        
        return response;
    }

    /**
     * Get weather forecast for a city.
     * Results are cached in Redis for 30 minutes.
     */
    @Cacheable(value = "weather:forecast", key = "#city.toLowerCase() + ':' + #days", unless = "#result.source == 'FALLBACK'")
    public ForecastResponse getForecast(String city, int days) {
        log.info("Fetching forecast for city: {}, days: {}", city, days);
        return weatherClient.getForecast(city, days);
    }

    /**
     * Get historical weather data for a city.
     */
    public List<WeatherResponse> getHistoricalData(String city, int hours) {
        log.info("Fetching historical data for city: {}, last {} hours", city, hours);
        
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<WeatherData> data = weatherRepository
            .findByCityIgnoreCaseAndTimestampBetweenOrderByTimestampDesc(
                city, since, LocalDateTime.now());
        
        return data.stream()
            .map(this::mapToWeatherResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get the most recent weather data for a city from database.
     */
    public Optional<WeatherResponse> getLastKnownWeather(String city) {
        return weatherRepository.findTopByCityIgnoreCaseOrderByTimestampDesc(city)
            .map(this::mapToWeatherResponse);
    }

    /**
     * Save weather data to database.
     */
    @Transactional
    public void saveWeatherData(WeatherResponse response) {
        WeatherData data = WeatherData.builder()
            .city(response.getCity())
            .country(response.getCountry())
            .temperature(response.getTemperature())
            .feelsLike(response.getFeelsLike())
            .humidity(response.getHumidity())
            .pressure(response.getPressure())
            .windSpeed(response.getWindSpeed())
            .windDirection(response.getWindDirection())
            .weatherMain(response.getWeatherMain())
            .weatherDescription(response.getWeatherDescription())
            .weatherIcon(response.getWeatherIcon())
            .cloudiness(response.getCloudiness())
            .visibility(response.getVisibility())
            .latitude(response.getLatitude())
            .longitude(response.getLongitude())
            .timestamp(response.getTimestamp())
            .build();

        weatherRepository.save(data);
        log.debug("Saved weather data for city: {}", response.getCity());
    }

    private Optional<Double> getPreviousTemperature(String city) {
        return weatherRepository.findTopByCityIgnoreCaseOrderByTimestampDesc(city)
            .map(WeatherData::getTemperature);
    }

    private WeatherResponse mapToWeatherResponse(WeatherData data) {
        return WeatherResponse.builder()
            .city(data.getCity())
            .country(data.getCountry())
            .temperature(data.getTemperature())
            .feelsLike(data.getFeelsLike())
            .humidity(data.getHumidity())
            .pressure(data.getPressure())
            .windSpeed(data.getWindSpeed())
            .windDirection(data.getWindDirection())
            .weatherMain(data.getWeatherMain())
            .weatherDescription(data.getWeatherDescription())
            .weatherIcon(data.getWeatherIcon())
            .cloudiness(data.getCloudiness())
            .visibility(data.getVisibility())
            .latitude(data.getLatitude())
            .longitude(data.getLongitude())
            .timestamp(data.getTimestamp())
            .source("DATABASE")
            .build();
    }

}
