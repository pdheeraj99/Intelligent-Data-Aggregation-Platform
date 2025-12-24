package com.idap.newsservice.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for article response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleResponse {
    private String id;
    private String sourceName;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private LocalDateTime publishedAt;
    private String category;
    private List<String> keywords;
    private String sentiment;
    private String source;
}
