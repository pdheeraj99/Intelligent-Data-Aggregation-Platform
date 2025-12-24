package com.idap.notificationservice.repository;

import com.idap.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientOrderByCreatedAtDesc(String recipient);

    List<Notification> findByStatusOrderByCreatedAtDesc(String status);

    List<Notification> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime since);

    long countByStatusAndCreatedAtAfter(String status, LocalDateTime since);
}
