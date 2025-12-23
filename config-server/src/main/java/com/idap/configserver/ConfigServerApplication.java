package com.idap.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Spring Cloud Config Server for IDAP.
 * Provides centralized configuration management for all microservices.
 * 
 * Configuration files are fetched from:
 * https://github.com/pdheeraj99/Intelligent-Data-Aggregation-Platform-Config-Server
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}
