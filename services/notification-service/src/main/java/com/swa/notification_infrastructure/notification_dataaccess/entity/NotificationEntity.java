package com.swa.notification_infrastructure.notification_dataaccess.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.swa.notification_domain.valueobject.NotificationTypes;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document(collection = "notifications")
public class NotificationEntity {
    @Id
    private String id;
    private NotificationTypes type;
    private LocalDateTime notificationDate;
    private Object orderConfirmationEvent;
}
