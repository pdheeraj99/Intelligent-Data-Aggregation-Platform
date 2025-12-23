package com.idap.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Rate limiting configuration for API Gateway.
 * Uses Redis to track request rates per user/IP.
 */
@Configuration
public class RateLimitConfig {

    /**
     * Rate limit by user ID (from JWT) or IP address for anonymous requests.
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Try to get user ID from the header (set by JwtAuthFilter)
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null) {
                return Mono.just(userId);
            }
            // Fall back to IP address for anonymous requests
            String ip = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "anonymous";
            return Mono.just(ip);
        };
    }

}
