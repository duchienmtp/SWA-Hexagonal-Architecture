package com.swa.notification_infrastructure.notification_messaging.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.swa.kafka.avro.model.CustomerAvro;
import com.swa.kafka.avro.model.OrderConfirmationEventAvro;
import com.swa.kafka.avro.model.OrderItemAvro;
import com.swa.notification_domain.entity.Customer;
import com.swa.notification_domain.entity.Notification;
import com.swa.notification_domain.entity.OrderItem;
import com.swa.notification_domain.event.OrderConfirmationEvent;
import com.swa.notification_domain.valueobject.Money;
import com.swa.notification_domain.valueobject.NotificationTypes;
import com.swa.notification_domain.valueobject.OrderId;
import com.swa.notification_domain.valueobject.ProductId;
import com.swa.notification_domain.valueobject.RestaurantId;
import com.swa.notification_infrastructure.notification_dataaccess.entity.NotificationEntity;

@Component
public class NotificationEventMapper {
    public OrderConfirmationEvent toOrderConfirmationEvent(OrderConfirmationEventAvro orderConfirmationEventAvro) {
        return OrderConfirmationEvent.builder()
                .orderId(OrderId.toOrderId(orderConfirmationEventAvro.getOrderId()))
                .restaurantId(RestaurantId.toRestaurantId(orderConfirmationEventAvro.getRestaurantId()))
                .totalAmount(new com.swa.notification_domain.valueobject.Money(new java.math.BigDecimal(orderConfirmationEventAvro.getTotalAmount())))
                .customer(toCustomer(orderConfirmationEventAvro.getCustomer()))
                .items(orderConfirmationEventAvro.getItems().stream()
                        .map(this::toOrderItem)
                        .toList())
                .build();
    }

    public Customer toCustomer(CustomerAvro customerAvro) {
        return Customer.builder()
                .id(customerAvro.getId())
                .fullName(customerAvro.getFullName())
                .email(customerAvro.getEmail())
                .build();
    }

    public OrderItem toOrderItem(OrderItemAvro orderItemAvro) {
        return OrderItem.builder()
                .productId(ProductId.toProductId(orderItemAvro.getProductId()))
                .price(new Money(new BigDecimal(orderItemAvro.getPrice())))
                .quantity(orderItemAvro.getQuantity())
                .build();
    }

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

    public Notification toNotification(OrderConfirmationEventAvro event) {
        return Notification.builder()
                .type(NotificationTypes.ORDER_CONFIRMATION)
                .notificationDate(LocalDateTime.now())
                .orderConfirmationEvent(event)
                .build();
    }
}
