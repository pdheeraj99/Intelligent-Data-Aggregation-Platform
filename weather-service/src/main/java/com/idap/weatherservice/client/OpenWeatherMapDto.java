package com.idap.weatherservice.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTOs for mapping OpenWeatherMap API responses.
 */
public class OpenWeatherMapDto {

    /**
     * Response from /weather endpoint (current weather).
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentWeatherResponse {
        private Coord coord;
        private List<Weather> weather;
        private String base;
        private Main main;
        private int visibility;
        private Wind wind;
        private Clouds clouds;
        private long dt;
        private Sys sys;
        private int timezone;
        private long id;
        private String name;
        private int cod;
    }

    /**
     * Response from /forecast endpoint (5-day forecast).
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastWeatherResponse {
        private String cod;
        private int message;
        private int cnt;
        private List<ForecastItem> list;
        private City city;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastItem {
        private long dt;
        private Main main;
        private List<Weather> weather;
        private Clouds clouds;
        private Wind wind;
        private int visibility;
        private double pop; // Probability of precipitation
        private Rain rain;
        private Snow snow;
        private Sys sys;
        @JsonProperty("dt_txt")
        private String dtTxt;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Coord {
        private double lon;
        private double lat;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private int id;
        private String main;
        private String description;
        private String icon;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private double temp;
        @JsonProperty("feels_like")
        private double feelsLike;
        @JsonProperty("temp_min")
        private double tempMin;
        @JsonProperty("temp_max")
        private double tempMax;
        private int pressure;
        private int humidity;
        @JsonProperty("sea_level")
        private Integer seaLevel;
        @JsonProperty("grnd_level")
        private Integer grndLevel;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        private double speed;
        private int deg;
        private Double gust;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Clouds {
        private int all;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        private int type;
        private long id;
        private String country;
        private long sunrise;
        private long sunset;
        private String pod; // Part of day (n = night, d = day)
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class City {
        private long id;
        private String name;
        private Coord coord;
        private String country;
        private int population;
        private int timezone;
        private long sunrise;
        private long sunset;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Rain {
        @JsonProperty("3h")
        private Double threeHour;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snow {
        @JsonProperty("3h")
        private Double threeHour;
    }

}
