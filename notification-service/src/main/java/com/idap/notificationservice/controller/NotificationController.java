package com.idap.notificationservice.controller;

import com.idap.notificationservice.dto.NotificationRequest;
import com.idap.notificationservice.model.Notification;
import com.idap.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Send a notification.
     * POST /api/notifications/send
     */
    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@RequestBody NotificationRequest request) {
        log.info("Send notification request to: {}", request.getRecipient());
        notificationService.sendNotification(request);
        return ResponseEntity.accepted().build();
    }

    /**
     * Get notifications for a recipient.
     * GET /api/notifications/recipient/{email}
     */
    @GetMapping("/recipient/{email}")
    public ResponseEntity<List<Notification>> getByRecipient(@PathVariable String email) {
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(email));
    }

    /**
     * Get recent notifications.
     * GET /api/notifications/recent?hours=24
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Notification>> getRecent(
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(notificationService.getRecentNotifications(hours));
    }

    /**
     * Get notification statistics.
     * GET /api/notifications/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Object> getStatistics() {
        return ResponseEntity.ok(notificationService.getStatistics());
    }
}
