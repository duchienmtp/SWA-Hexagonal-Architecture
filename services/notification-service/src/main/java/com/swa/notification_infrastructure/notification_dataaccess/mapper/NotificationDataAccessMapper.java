package com.swa.notification_infrastructure.notification_dataaccess.mapper;

import org.springframework.stereotype.Component;

import com.swa.notification_domain.entity.Notification;
import com.swa.notification_infrastructure.notification_dataaccess.entity.NotificationEntity;

@Component
public class NotificationDataAccessMapper {

    // Domain → JPA (for saving)
    public NotificationEntity toJpaEntity(Notification notification) {
        return NotificationEntity.builder()
            .id(notification.getId())
            .type(notification.getType())
            .notificationDate(notification.getNotificationDate())
            .orderConfirmationEvent(notification.getOrderConfirmationEvent())
            .build();
    }
    
    // JPA → Domain (after loading)
    public Notification toDomain(NotificationEntity entity) {
        return Notification.builder()
            .id(entity.getId())
            .type(entity.getType())
            .notificationDate(entity.getNotificationDate())
            .orderConfirmationEvent(entity.getOrderConfirmationEvent())
            .build();
    }
}