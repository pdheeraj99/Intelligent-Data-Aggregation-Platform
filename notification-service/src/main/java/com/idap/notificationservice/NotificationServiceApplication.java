package com.idap.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;
import jakarta.annotation.PostConstruct;

/**
 * Notification Service - Sends alerts via email using SendGrid.
 * Features: RabbitMQ consumer, SendGrid email, notification preferences.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
public class NotificationServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}

