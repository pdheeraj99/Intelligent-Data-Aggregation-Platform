package com.idap.newsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * News Aggregation Service - Fetches and stores news from NewsAPI.
 * Features: MongoDB storage, Kafka event publishing, article search.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class NewsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsServiceApplication.class, args);
    }
}
