package com.idap.weatherservice.client;

import com.idap.weatherservice.dto.ForecastResponse;
import com.idap.weatherservice.dto.WeatherResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST client for OpenWeatherMap API.
 * Uses RestClient (Spring 6+) for HTTP calls.
 * Implements circuit breaker pattern for resilience.
 */
@Component
@Slf4j
public class OpenWeatherMapClient {

    private final RestClient restClient;
    private final String apiKey;
    private final boolean apiKeyConfigured;

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5";

    public OpenWeatherMapClient(
            @Value("${openweathermap.api-key:}") String apiKey) {
        this.apiKey = apiKey;
        this.apiKeyConfigured = apiKey != null && !apiKey.isBlank() && !apiKey.equals("your-api-key-here");
        this.restClient = RestClient.builder()
            .baseUrl(BASE_URL)
            .build();
        
        if (!apiKeyConfigured) {
            log.warn("OpenWeatherMap API key not configured. Using mock data.");
        }
    }

    /**
     * Fetch current weather for a city.
     */
    @CircuitBreaker(name = "openweathermap", fallbackMethod = "getCurrentWeatherFallback")
    public WeatherResponse getCurrentWeather(String city) {
        if (!apiKeyConfigured) {
            return getMockCurrentWeather(city);
        }

        log.info("Fetching current weather for city: {}", city);

        OpenWeatherMapDto.CurrentWeatherResponse response = restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/weather")
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .build())
            .retrieve()
            .body(OpenWeatherMapDto.CurrentWeatherResponse.class);

        return mapToWeatherResponse(response);
    }

    /**
     * Fetch 5-day forecast for a city.
     */
    @CircuitBreaker(name = "openweathermap", fallbackMethod = "getForecastFallback")
    public ForecastResponse getForecast(String city, int days) {
        if (!apiKeyConfigured) {
            return getMockForecast(city, days);
        }

        log.info("Fetching forecast for city: {}, days: {}", city, days);

        OpenWeatherMapDto.ForecastWeatherResponse response = restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/forecast")
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .queryParam("cnt", days * 8) // 8 forecasts per day (3-hour intervals)
                .build())
            .retrieve()
            .body(OpenWeatherMapDto.ForecastWeatherResponse.class);

        return mapToForecastResponse(response);
    }

    // Fallback methods for circuit breaker
    public WeatherResponse getCurrentWeatherFallback(String city, Throwable t) {
        log.error("Circuit breaker triggered for getCurrentWeather. City: {}, Error: {}", city, t.getMessage());
        return getMockCurrentWeather(city);
    }

    public ForecastResponse getForecastFallback(String city, int days, Throwable t) {
        log.error("Circuit breaker triggered for getForecast. City: {}, Error: {}", city, t.getMessage());
        return getMockForecast(city, days);
    }

    // Mapping methods
    private WeatherResponse mapToWeatherResponse(OpenWeatherMapDto.CurrentWeatherResponse response) {
        if (response == null || response.getWeather() == null || response.getWeather().isEmpty()) {
            throw new RuntimeException("Invalid response from OpenWeatherMap API");
        }

        OpenWeatherMapDto.Weather weather = response.getWeather().get(0);

        return WeatherResponse.builder()
            .city(response.getName())
            .country(response.getSys().getCountry())
            .temperature(response.getMain().getTemp())
            .feelsLike(response.getMain().getFeelsLike())
            .humidity(response.getMain().getHumidity())
            .pressure(response.getMain().getPressure())
            .windSpeed(response.getWind().getSpeed())
            .windDirection(getWindDirection(response.getWind().getDeg()))
            .weatherMain(weather.getMain())
            .weatherDescription(weather.getDescription())
            .weatherIcon(weather.getIcon())
            .cloudiness(response.getClouds().getAll())
            .visibility(response.getVisibility())
            .latitude(response.getCoord().getLat())
            .longitude(response.getCoord().getLon())
            .timestamp(LocalDateTime.ofInstant(
                Instant.ofEpochSecond(response.getDt()), ZoneId.systemDefault()))
            .source("API")
            .build();
    }

    private ForecastResponse mapToForecastResponse(OpenWeatherMapDto.ForecastWeatherResponse response) {
        if (response == null || response.getList() == null) {
            throw new RuntimeException("Invalid response from OpenWeatherMap API");
        }

        List<ForecastResponse.ForecastDay> forecastDays = response.getList().stream()
            .map(item -> {
                OpenWeatherMapDto.Weather weather = item.getWeather().get(0);
                return ForecastResponse.ForecastDay.builder()
                    .dateTime(LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(item.getDt()), ZoneId.systemDefault()))
                    .temperature(item.getMain().getTemp())
                    .temperatureMin(item.getMain().getTempMin())
                    .temperatureMax(item.getMain().getTempMax())
                    .feelsLike(item.getMain().getFeelsLike())
                    .humidity(item.getMain().getHumidity())
                    .pressure(item.getMain().getPressure())
                    .windSpeed(item.getWind().getSpeed())
                    .weatherMain(weather.getMain())
                    .weatherDescription(weather.getDescription())
                    .weatherIcon(weather.getIcon())
                    .cloudiness(item.getClouds().getAll())
                    .probabilityOfPrecipitation(item.getPop() * 100)
                    .build();
            })
            .collect(Collectors.toList());

        return ForecastResponse.builder()
            .city(response.getCity().getName())
            .country(response.getCity().getCountry())
            .latitude(response.getCity().getCoord().getLat())
            .longitude(response.getCity().getCoord().getLon())
            .forecast(forecastDays)
            .source("API")
            .build();
    }

    // Mock data for testing without API key
    private WeatherResponse getMockCurrentWeather(String city) {
        log.info("Returning mock weather data for city: {}", city);
        return WeatherResponse.builder()
            .city(city)
            .country("XX")
            .temperature(22.5)
            .feelsLike(21.0)
            .humidity(65)
            .pressure(1013)
            .windSpeed(5.5)
            .windDirection("NW")
            .weatherMain("Clouds")
            .weatherDescription("scattered clouds")
            .weatherIcon("03d")
            .cloudiness(40)
            .visibility(10000)
            .latitude(0.0)
            .longitude(0.0)
            .timestamp(LocalDateTime.now())
            .source("MOCK")
            .build();
    }

    private ForecastResponse getMockForecast(String city, int days) {
        log.info("Returning mock forecast data for city: {}, days: {}", city, days);
        List<ForecastResponse.ForecastDay> mockForecast = java.util.stream.IntStream.range(0, days * 8)
            .mapToObj(i -> ForecastResponse.ForecastDay.builder()
                .dateTime(LocalDateTime.now().plusHours(i * 3))
                .temperature(20.0 + (i % 10))
                .temperatureMin(18.0 + (i % 5))
                .temperatureMax(25.0 + (i % 8))
                .feelsLike(19.0 + (i % 10))
                .humidity(60 + (i % 20))
                .pressure(1010 + (i % 10))
                .windSpeed(3.0 + (i % 5))
                .weatherMain(i % 3 == 0 ? "Clear" : i % 3 == 1 ? "Clouds" : "Rain")
                .weatherDescription("Mock weather description")
                .weatherIcon("03d")
                .cloudiness(30 + (i % 40))
                .probabilityOfPrecipitation((double) (i % 100))
                .build())
            .collect(Collectors.toList());

        return ForecastResponse.builder()
            .city(city)
            .country("XX")
            .latitude(0.0)
            .longitude(0.0)
            .forecast(mockForecast)
            .source("MOCK")
            .build();
    }

    private String getWindDirection(int degrees) {
        String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                               "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        int index = (int) Math.round(((double) degrees % 360) / 22.5);
        return directions[index % 16];
    }

}
