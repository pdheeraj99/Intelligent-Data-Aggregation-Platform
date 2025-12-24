package com.idap.newsservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MongoDB document representing a news article.
 */
@Document(collection = "articles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    @Id
    private String id;

    @Indexed
    private String sourceId;

    private String sourceName;

    private String author;

    @TextIndexed(weight = 2)
    private String title;

    @TextIndexed
    private String description;

    private String url;

    private String urlToImage;

    private LocalDateTime publishedAt;

    @TextIndexed
    private String content;

    @Indexed
    private String category;

    @Indexed
    private List<String> keywords;

    private String sentiment;  // POSITIVE, NEGATIVE, NEUTRAL

    private Double sentimentScore;

    @Indexed
    private LocalDateTime fetchedAt;

    private String source;  // NEWSAPI, MOCK
}
