package com.idap.analyticsservice.controller;

import com.idap.analyticsservice.model.Insight;
import com.idap.analyticsservice.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Get all active insights.
     * GET /api/insights
     */
    @GetMapping("/insights")
    public ResponseEntity<List<Insight>> getActiveInsights() {
        return ResponseEntity.ok(analyticsService.getActiveInsights());
    }

    /**
     * Get top insights by score.
     * GET /api/insights/top
     */
    @GetMapping("/insights/top")
    public ResponseEntity<List<Insight>> getTopInsights() {
        return ResponseEntity.ok(analyticsService.getTopInsights());
    }

    /**
     * Get insights by type.
     * GET /api/insights/type/{type}
     */
    @GetMapping("/insights/type/{type}")
    public ResponseEntity<List<Insight>> getInsightsByType(@PathVariable String type) {
        return ResponseEntity.ok(analyticsService.getInsightsByType(type));
    }

    /**
     * Get recent insights.
     * GET /api/insights/recent?hours=24
     */
    @GetMapping("/insights/recent")
    public ResponseEntity<List<Insight>> getRecentInsights(
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(analyticsService.getRecentInsights(hours));
    }

    /**
     * Get analytics summary.
     * GET /api/analytics/summary
     */
    @GetMapping("/analytics/summary")
    public ResponseEntity<Object> getSummary() {
        return ResponseEntity.ok(java.util.Map.of(
            "activeInsights", analyticsService.getActiveInsights().size(),
            "topInsights", analyticsService.getTopInsights(),
            "generatedAt", java.time.LocalDateTime.now()
        ));
    }
}
