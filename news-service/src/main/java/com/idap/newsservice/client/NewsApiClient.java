package com.idap.newsservice.client;

import com.idap.newsservice.dto.ArticleResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Client for NewsAPI.org - fetches news articles.
 * Includes circuit breaker and mock fallback.
 */
@Component
@Slf4j
public class NewsApiClient {

    private final WebClient webClient;
    private final String apiKey;
    private final Random random = new Random();

    public NewsApiClient(
            WebClient.Builder webClientBuilder,
            @Value("${newsapi.api.key:demo}") String apiKey,
            @Value("${newsapi.api.base-url:https://newsapi.org/v2}") String baseUrl) {
        this.apiKey = apiKey;
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "newsapi", fallbackMethod = "getTopHeadlinesFallback")
    public List<ArticleResponse> getTopHeadlines(String category, String country) {
        if ("demo".equals(apiKey)) {
            log.info("Using mock data for news (API key not configured)");
            return generateMockArticles(category, 10);
        }

        log.info("Fetching top headlines for category: {}, country: {}", category, country);
        try {
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/top-headlines")
                            .queryParam("category", category)
                            .queryParam("country", country)
                            .queryParam("apiKey", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return parseArticles(response, category);
        } catch (Exception e) {
            log.error("Error fetching headlines: {}", e.getMessage());
            return generateMockArticles(category, 5);
        }
    }

    public List<ArticleResponse> getTopHeadlinesFallback(String category, String country, Throwable t) {
        log.warn("NewsAPI circuit breaker open: {}", t.getMessage());
        return generateMockArticles(category, 5);
    }

    @CircuitBreaker(name = "newsapi", fallbackMethod = "searchNewsFallback")
    public List<ArticleResponse> searchNews(String query) {
        if ("demo".equals(apiKey)) {
            return generateMockArticles("general", 10);
        }

        log.info("Searching news for: {}", query);
        try {
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/everything")
                            .queryParam("q", query)
                            .queryParam("sortBy", "publishedAt")
                            .queryParam("apiKey", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return parseArticles(response, "search");
        } catch (Exception e) {
            log.error("Error searching news: {}", e.getMessage());
            return generateMockArticles("general", 5);
        }
    }

    public List<ArticleResponse> searchNewsFallback(String query, Throwable t) {
        log.warn("NewsAPI search circuit breaker open: {}", t.getMessage());
        return generateMockArticles("general", 5);
    }

    @SuppressWarnings("unchecked")
    private List<ArticleResponse> parseArticles(Map<String, Object> response, String category) {
        if (response == null || !"ok".equals(response.get("status"))) {
            return generateMockArticles(category, 5);
        }

        List<Map<String, Object>> articles = (List<Map<String, Object>>) response.get("articles");
        if (articles == null) return Collections.emptyList();

        return articles.stream()
                .map(article -> {
                    Map<String, Object> source = (Map<String, Object>) article.get("source");
                    return ArticleResponse.builder()
                            .sourceName(source != null ? (String) source.get("name") : "Unknown")
                            .author((String) article.get("author"))
                            .title((String) article.get("title"))
                            .description((String) article.get("description"))
                            .url((String) article.get("url"))
                            .urlToImage((String) article.get("urlToImage"))
                            .publishedAt(LocalDateTime.now())
                            .category(category)
                            .source("NEWSAPI")
                            .build();
                })
                .toList();
    }

    private List<ArticleResponse> generateMockArticles(String category, int count) {
        List<ArticleResponse> articles = new ArrayList<>();
        String[] titles = getMockTitles(category);
        String[] sources = {"Reuters", "BBC News", "CNN", "Bloomberg", "TechCrunch", "The Guardian"};

        for (int i = 0; i < count && i < titles.length; i++) {
            articles.add(ArticleResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .sourceName(sources[random.nextInt(sources.length)])
                    .author("Staff Reporter")
                    .title(titles[i])
                    .description("This is a mock description for the article about " + category)
                    .url("https://example.com/news/" + i)
                    .urlToImage("https://picsum.photos/800/400?random=" + i)
                    .publishedAt(LocalDateTime.now().minusHours(random.nextInt(24)))
                    .category(category)
                    .keywords(Arrays.asList(category, "news", "trending"))
                    .sentiment(random.nextBoolean() ? "POSITIVE" : "NEUTRAL")
                    .source("MOCK")
                    .build());
        }
        return articles;
    }

    private String[] getMockTitles(String category) {
        return switch (category.toLowerCase()) {
            case "technology" -> new String[]{
                "AI Revolution Continues as New Models Emerge",
                "Tech Giants Report Strong Q4 Earnings",
                "Quantum Computing Makes Major Breakthrough",
                "Cybersecurity Concerns Rise in 2024",
                "New Smartphone Features Set Industry Standards"
            };
            case "business" -> new String[]{
                "Markets Rally on Positive Economic Data",
                "Federal Reserve Signals Policy Shift",
                "Global Trade Agreements Reshape Markets",
                "Startup Ecosystem Sees Record Investment",
                "Corporate Earnings Season Exceeds Expectations"
            };
            case "science" -> new String[]{
                "NASA Announces New Space Mission",
                "Climate Scientists Release New Report",
                "Medical Research Shows Promising Results",
                "Renewable Energy Hits New Milestone",
                "Archaeological Discovery Changes History"
            };
            default -> new String[]{
                "Breaking: Major Developments Unfold",
                "Global Leaders Meet for Summit",
                "Economic Indicators Show Growth",
                "Innovation Drives Industry Change",
                "Community Response to Recent Events"
            };
        };
    }
}
