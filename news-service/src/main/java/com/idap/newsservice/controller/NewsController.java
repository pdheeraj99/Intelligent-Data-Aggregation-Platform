package com.idap.newsservice.controller;

import com.idap.newsservice.dto.ArticleResponse;
import com.idap.newsservice.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@Slf4j
public class NewsController {

    private final NewsService newsService;

    /**
     * Get top headlines by category.
     * GET /api/news/headlines?category=technology&country=us
     */
    @GetMapping("/headlines")
    public ResponseEntity<List<ArticleResponse>> getTopHeadlines(
            @RequestParam(defaultValue = "general") String category,
            @RequestParam(defaultValue = "us") String country) {
        log.info("Request for headlines: category={}, country={}", category, country);
        return ResponseEntity.ok(newsService.getTopHeadlines(category, country));
    }

    /**
     * Search news articles.
     * GET /api/news/search?q=technology
     */
    @GetMapping("/search")
    public ResponseEntity<List<ArticleResponse>> searchNews(@RequestParam String q) {
        log.info("Search request: {}", q);
        return ResponseEntity.ok(newsService.searchNews(q));
    }

    /**
     * Get articles by category from database.
     * GET /api/news/category/{category}?page=0&size=10
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ArticleResponse>> getByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(newsService.getArticlesByCategory(category, page, size));
    }

    /**
     * Full-text search in stored articles.
     * GET /api/news/articles/search?text=AI&page=0&size=10
     */
    @GetMapping("/articles/search")
    public ResponseEntity<Page<ArticleResponse>> searchArticles(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(newsService.searchArticles(text, page, size));
    }

    /**
     * Get recent articles.
     * GET /api/news/recent?hours=24
     */
    @GetMapping("/recent")
    public ResponseEntity<List<ArticleResponse>> getRecentArticles(
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(newsService.getRecentArticles(hours));
    }
}
