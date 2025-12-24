package com.idap.notificationservice.consumer;

import com.idap.notificationservice.dto.NotificationRequest;
import com.idap.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "notification.email.queue")
    public void receiveEmailNotification(NotificationRequest request) {
        log.info("Received email notification request for: {}", request.getRecipient());
        notificationService.sendNotification(request);
    }

    @RabbitListener(queues = "notification.alert.queue")
    public void receiveAlertNotification(NotificationRequest request) {
        log.info("Received alert notification: {}", request.getSubject());
        request.setType("EMAIL");  // Default to email for alerts
        notificationService.sendNotification(request);
    }
}
