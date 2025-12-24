package com.idap.newsservice.service;

import com.idap.newsservice.client.NewsApiClient;
import com.idap.newsservice.dto.ArticleResponse;
import com.idap.newsservice.model.Article;
import com.idap.newsservice.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {

    private final NewsApiClient newsApiClient;
    private final ArticleRepository articleRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String NEWS_TOPIC = "news.article.published";

    public List<ArticleResponse> getTopHeadlines(String category, String country) {
        log.info("Getting top headlines for category: {}", category);
        List<ArticleResponse> articles = newsApiClient.getTopHeadlines(category, country);
        
        // Save to MongoDB
        articles.forEach(this::saveArticle);
        
        return articles;
    }

    public List<ArticleResponse> searchNews(String query) {
        log.info("Searching news for: {}", query);
        return newsApiClient.searchNews(query);
    }

    public Page<ArticleResponse> getArticlesByCategory(String category, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return articleRepository.findByCategory(category, pageRequest)
                .map(this::mapToResponse);
    }

    public Page<ArticleResponse> searchArticles(String searchText, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return articleRepository.searchByText(searchText, pageRequest)
                .map(this::mapToResponse);
    }

    public List<ArticleResponse> getRecentArticles(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return articleRepository.findRecentArticles(since).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void saveArticle(ArticleResponse response) {
        try {
            Article article = Article.builder()
                    .sourceName(response.getSourceName())
                    .author(response.getAuthor())
                    .title(response.getTitle())
                    .description(response.getDescription())
                    .url(response.getUrl())
                    .urlToImage(response.getUrlToImage())
                    .publishedAt(response.getPublishedAt())
                    .category(response.getCategory())
                    .keywords(response.getKeywords())
                    .sentiment(response.getSentiment())
                    .fetchedAt(LocalDateTime.now())
                    .source(response.getSource())
                    .build();

            articleRepository.save(article);
            publishNewsEvent(response);
        } catch (Exception e) {
            log.error("Failed to save article: {}", e.getMessage());
        }
    }

    private void publishNewsEvent(ArticleResponse article) {
        try {
            kafkaTemplate.send(NEWS_TOPIC, article.getCategory(), article);
            log.debug("Published news event for: {}", article.getTitle());
        } catch (Exception e) {
            log.error("Failed to publish news event: {}", e.getMessage());
        }
    }

    private ArticleResponse mapToResponse(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .sourceName(article.getSourceName())
                .author(article.getAuthor())
                .title(article.getTitle())
                .description(article.getDescription())
                .url(article.getUrl())
                .urlToImage(article.getUrlToImage())
                .publishedAt(article.getPublishedAt())
                .category(article.getCategory())
                .keywords(article.getKeywords())
                .sentiment(article.getSentiment())
                .source(article.getSource())
                .build();
    }
}
