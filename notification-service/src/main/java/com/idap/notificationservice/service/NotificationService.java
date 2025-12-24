package com.idap.notificationservice.service;

import com.idap.notificationservice.dto.NotificationRequest;
import com.idap.notificationservice.model.Notification;
import com.idap.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Value("${notification.email.from:noreply@idap.example.com}")
    private String fromEmail;

    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;

    @Async
    public void sendNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .type(request.getType())
                .recipient(request.getRecipient())
                .subject(request.getSubject())
                .content(request.getContent())
                .build();

        try {
            if ("EMAIL".equals(request.getType())) {
                sendEmail(request);
            }
            
            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
            log.info("Notification sent successfully to: {}", request.getRecipient());
        } catch (Exception e) {
            notification.setStatus("FAILED");
            notification.setErrorMessage(e.getMessage());
            log.error("Failed to send notification: {}", e.getMessage());
        }

        notificationRepository.save(notification);
    }

    private void sendEmail(NotificationRequest request) {
        if (!emailEnabled) {
            log.info("Email disabled. Mock sending to: {} - Subject: {}", 
                    request.getRecipient(), request.getSubject());
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(request.getRecipient());
        message.setSubject(request.getSubject());
        message.setText(request.getContent());
        
        mailSender.send(message);
    }

    public Notification createNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .type(request.getType())
                .recipient(request.getRecipient())
                .subject(request.getSubject())
                .content(request.getContent())
                .status("PENDING")
                .build();
        
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient);
    }

    public List<Notification> getRecentNotifications(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return notificationRepository.findByCreatedAtAfterOrderByCreatedAtDesc(since);
    }

    public Object getStatistics() {
        LocalDateTime last24h = LocalDateTime.now().minusHours(24);
        return java.util.Map.of(
            "sentLast24h", notificationRepository.countByStatusAndCreatedAtAfter("SENT", last24h),
            "failedLast24h", notificationRepository.countByStatusAndCreatedAtAfter("FAILED", last24h),
            "pendingLast24h", notificationRepository.countByStatusAndCreatedAtAfter("PENDING", last24h)
        );
    }
}
