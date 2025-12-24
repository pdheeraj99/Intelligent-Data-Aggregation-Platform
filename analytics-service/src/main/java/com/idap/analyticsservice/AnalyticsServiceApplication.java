package com.idap.analyticsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;
import jakarta.annotation.PostConstruct;

/**
 * Analytics & Correlation Service - Consumes events and generates insights.
 * Features: Kafka consumers, data correlation, CQRS read model.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class AnalyticsServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(AnalyticsServiceApplication.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}

