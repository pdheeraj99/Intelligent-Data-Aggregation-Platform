package com.idap.weatherservice.repository;

import com.idap.weatherservice.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for WeatherData entity.
 */
@Repository
public interface WeatherRepository extends JpaRepository<WeatherData, Long> {

    /**
     * Find the most recent weather data for a city.
     */
    Optional<WeatherData> findTopByCityIgnoreCaseOrderByTimestampDesc(String city);

    /**
     * Find weather data for a city within a time range.
     */
    List<WeatherData> findByCityIgnoreCaseAndTimestampBetweenOrderByTimestampDesc(
        String city, LocalDateTime start, LocalDateTime end);

    /**
     * Find recent weather data for a city (limited results).
     */
    List<WeatherData> findTop24ByCityIgnoreCaseOrderByTimestampDesc(String city);

    /**
     * Get average temperature for a city over a period.
     */
    @Query("SELECT AVG(w.temperature) FROM WeatherData w WHERE LOWER(w.city) = LOWER(:city) AND w.timestamp >= :since")
    Optional<Double> getAverageTemperature(@Param("city") String city, @Param("since") LocalDateTime since);

    /**
     * Check if weather data exists for a city within the last N minutes.
     */
    @Query("SELECT COUNT(w) > 0 FROM WeatherData w WHERE LOWER(w.city) = LOWER(:city) AND w.timestamp >= :since")
    boolean existsRecentData(@Param("city") String city, @Param("since") LocalDateTime since);

}
