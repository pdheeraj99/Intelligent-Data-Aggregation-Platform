package com.idap.notificationservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private String type;
    private String recipient;
    private String subject;
    private String content;
    private String priority;
}
