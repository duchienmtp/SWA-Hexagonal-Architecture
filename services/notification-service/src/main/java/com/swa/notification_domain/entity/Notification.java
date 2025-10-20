package com.swa.notification_domain.entity;

import java.time.LocalDateTime;

import com.swa.notification_domain.valueobject.NotificationTypes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Notification {
    private String id;
    private NotificationTypes type;
    private LocalDateTime notificationDate;
    private Object orderConfirmationEvent;
}
