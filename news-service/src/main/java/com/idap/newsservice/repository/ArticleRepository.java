package com.idap.newsservice.repository;

import com.idap.newsservice.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {

    Page<Article> findByCategory(String category, Pageable pageable);

    Page<Article> findBySourceName(String sourceName, Pageable pageable);

    @Query("{ 'publishedAt': { $gte: ?0 } }")
    List<Article> findRecentArticles(LocalDateTime since);

    @Query("{ '$text': { '$search': ?0 } }")
    Page<Article> searchByText(String searchText, Pageable pageable);

    @Query("{ 'keywords': { $in: ?0 } }")
    Page<Article> findByKeywords(List<String> keywords, Pageable pageable);

    List<Article> findTop10ByCategoryOrderByPublishedAtDesc(String category);

    long countByPublishedAtAfter(LocalDateTime since);
}
