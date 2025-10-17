package com.swa.notification_infrastructure.notification_messaging.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.swa.kafka.avro.model.OrderConfirmationEventAvro;
import com.swa.notification_domain.event.OrderConfirmationEvent;
import com.swa.notification_domain.valueobject.NotificationTypes;
import com.swa.notification_infrastructure.notification_dataaccess.entity.NotificationEntity;

@Component
public class NotificationEventMapper {
    public NotificationEntity toNotificationEntity(OrderConfirmationEventAvro orderConfirmationEvent) {
        return NotificationEntity.builder()
                .type(NotificationTypes.ORDER_CONFIRMATION)
                .notificationDate(LocalDateTime.now())
                .orderConfirmationEvent(orderConfirmationEvent)
                .build();
    }

    public NotificationEntity toNotificationEntity(OrderConfirmationEvent orderConfirmationEvent) {
        return NotificationEntity.builder()
                .type(NotificationTypes.ORDER_CONFIRMATION)
                .notificationDate(LocalDateTime.now())
                .orderConfirmationEvent(orderConfirmationEvent)
                .build();
    }
}
