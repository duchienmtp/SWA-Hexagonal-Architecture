package com.swa.notification_application.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.swa.notification_domain.entity.Notification;
import com.swa.notification_domain.event.OrderConfirmationEvent;
import com.swa.notification_domain.valueobject.NotificationTypes;

@Component
public class NotificationEntityMapper {
    public Notification toNotificationEntity(OrderConfirmationEvent orderConfirmationEvent) {
        return Notification.builder()
                .type(NotificationTypes.ORDER_CONFIRMATION)
                .notificationDate(LocalDateTime.now())
                .orderConfirmationEvent(orderConfirmationEvent)
                .build();
    }
}
