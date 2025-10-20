package com.swa.restaurant_infrastructure.restaurant_messaging.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.swa.kafka.avro.model.OrderApprovalEventAvro;
import com.swa.kafka.avro.model.OrderItemAvro;
import com.swa.kafka.avro.model.OrderPrepareEventAvro;
import com.swa.kafka.avro.model.ProcessPaymentFailedEventAvro;
import com.swa.kafka.avro.model.RestaurantInventoryRollbackEventAvro;
import com.swa.restaurant_domain.entity.OrderItem;
import com.swa.restaurant_domain.event.OrderPrepare;
import com.swa.restaurant_domain.event.OrderPrepareEvent;
import com.swa.restaurant_domain.event.RestaurantInventoryRollbackEvent;
import com.swa.restaurant_domain.valueobject.CustomerId;
import com.swa.restaurant_domain.valueobject.Money;
import com.swa.restaurant_domain.valueobject.OrderId;
import com.swa.restaurant_domain.valueobject.ProductId;
import com.swa.restaurant_domain.valueobject.RestaurantId;

@Component
public class RestaurantEventMapper {
    public OrderPrepare toOrderPrepareEvent(OrderPrepareEventAvro eventAvro) {
        return OrderPrepare.builder()
                .orderId(OrderId.toOrderId(eventAvro.getOrderId()))
                .customerId(CustomerId.toCustomerId(eventAvro.getCustomerId()))
                .restaurantId(RestaurantId.toRestaurantId(eventAvro.getRestaurantId()))
                .orderItems(eventAvro.getItems().stream()
                        .map(this::toOrderItem)
                        .toList())
                .build();
    }

    public OrderItem toOrderItem(OrderItemAvro orderItemAvro) {
        return OrderItem.builder()
                .productId(ProductId.toProductId(orderItemAvro.getProductId()))
                .price(new Money(new BigDecimal(orderItemAvro.getPrice())))
                .quantity(orderItemAvro.getQuantity())
                .build();
    }

    public ProcessPaymentFailedEventAvro toProcessPaymentFailedEventAvro(
            OrderPrepareEvent event,
            OrderPrepare orderPrepare) {
        return ProcessPaymentFailedEventAvro.newBuilder()
                .setOrderId(orderPrepare.getOrderId().getValue().toString())
                .setCustomerId(orderPrepare.getCustomerId().getValue().toString())
                .setMessage(event.getMessage())
                .build();
    }

    public OrderApprovalEventAvro toOrderApprovalEventAvro(
            OrderPrepareEvent event,
            OrderPrepare orderPrepare) {

        return OrderApprovalEventAvro.newBuilder()
                .setOrderId(orderPrepare.getOrderId().getValue().toString())
                .setCustomerId(orderPrepare.getCustomerId().getValue().toString())
                .setMessage(event.getMessage())
                .build();
    }

    public RestaurantInventoryRollbackEvent toRestaurantInventoryRollbackEvent(
            RestaurantInventoryRollbackEventAvro eventAvro) {
        return RestaurantInventoryRollbackEvent.builder()
                .orderId(OrderId.toOrderId(eventAvro.getOrderId()))
                .restaurantId(RestaurantId.toRestaurantId(eventAvro.getRestaurantId()))
                .customerId(CustomerId.toCustomerId(eventAvro.getCustomerId()))
                .orderItems(eventAvro.getItems().stream()
                        .map(this::toOrderItem)
                        .toList())
                .message(eventAvro.getMessage())
                .build();
    }

    public ProcessPaymentFailedEventAvro toProcessPaymentFailedEventAvro(
            OrderPrepareEvent event,
            RestaurantInventoryRollbackEvent rollbackEvent) {
        return ProcessPaymentFailedEventAvro.newBuilder()
                .setOrderId(rollbackEvent.getOrderId().getValue().toString())
                .setCustomerId(rollbackEvent.getCustomerId().getValue().toString())
                .setMessage(event.getMessage())
                .build();
    }
}
