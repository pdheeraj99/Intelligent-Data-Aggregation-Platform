package com.idap.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Spring Cloud Gateway for IDAP.
 * Single entry point for all microservices with:
 * - Dynamic routing via Eureka discovery
 * - JWT authentication filter
 * - Rate limiting (Redis-backed)
 * - Circuit breaker (Resilience4j)
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

}
