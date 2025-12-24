package com.idap.analyticsservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity for storing aggregated insights.
 */
@Entity
@Table(name = "insights")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;  // WEATHER_STOCK_CORRELATION, NEWS_SENTIMENT, MARKET_TREND

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    private Double score;  // Relevance or confidence score

    @Column(columnDefinition = "TEXT")
    private String data;  // JSON data for additional details

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    private LocalDateTime expiresAt;

    private Boolean active;
}
