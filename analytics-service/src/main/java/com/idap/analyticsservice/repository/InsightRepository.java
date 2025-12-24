package com.idap.analyticsservice.repository;

import com.idap.analyticsservice.model.Insight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InsightRepository extends JpaRepository<Insight, Long> {

    List<Insight> findByTypeOrderByGeneratedAtDesc(String type);

    List<Insight> findByCategoryOrderByGeneratedAtDesc(String category);

    List<Insight> findByActiveTrueOrderByGeneratedAtDesc();

    List<Insight> findByGeneratedAtAfterOrderByGeneratedAtDesc(LocalDateTime since);

    List<Insight> findTop10ByActiveTrueOrderByScoreDesc();
}
