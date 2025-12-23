package com.idap.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Fallback controller for when downstream services are unavailable.
 * Returns graceful error responses instead of 500 errors.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "service", "user-service",
                        "message", "User service is temporarily unavailable. Please try again later.",
                        "timestamp", LocalDateTime.now().toString(),
                        "status", 503)));
    }

    @GetMapping("/weather-service")
    public Mono<ResponseEntity<Map<String, Object>>> weatherServiceFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "service", "weather-service",
                        "message", "Weather service is temporarily unavailable. Please try again later.",
                        "timestamp", LocalDateTime.now().toString(),
                        "status", 503)));
    }

    @GetMapping("/financial-service")
    public Mono<ResponseEntity<Map<String, Object>>> financialServiceFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "service", "financial-service",
                        "message", "Financial service is temporarily unavailable. Please try again later.",
                        "timestamp", LocalDateTime.now().toString(),
                        "status", 503)));
    }

    @GetMapping("/news-service")
    public Mono<ResponseEntity<Map<String, Object>>> newsServiceFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "service", "news-service",
                        "message", "News service is temporarily unavailable. Please try again later.",
                        "timestamp", LocalDateTime.now().toString(),
                        "status", 503)));
    }

}
