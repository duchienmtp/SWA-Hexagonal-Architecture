package com.swa.order_infrastructure.order_dataaccess.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.swa.order_infrastructure.order_dataaccess.entity.OrderItemJpaEntity;
import com.swa.order_infrastructure.order_dataaccess.entity.OrderJpaEntity;
import com.swa.order_domain.entity.Order;
import com.swa.order_domain.entity.OrderItem;
import com.swa.order_domain.valueobject.*;

@Component
public class OrderDataAccessMapper {

    // Domain → JPA (for saving)
    public OrderJpaEntity toJpaEntity(Order order) {
        return OrderJpaEntity.builder()
            .id(order.getId() != null ? order.getId().getValue() : null)
            .trackingId(order.getTrackingId() != null ? order.getTrackingId().getValue() : null)
            .customerId(order.getCustomerId() != null ? order.getCustomerId().getValue() : null)
            .restaurantId(order.getRestaurantId() != null ? order.getRestaurantId().getValue() : null)
            .status(order.getOrderStatus() != null ? order.getOrderStatus() : null)
            .totalAmount(order.getPrice().getAmount() != null ? order.getPrice().getAmount() : null)
            .items(order.getItems() != null ? order.getItems().stream()
            .<OrderItemJpaEntity>map(item -> OrderItemDataAccessMapper.toJpaEntity(item, OrderJpaEntity.builder().id(item.getOrderId().getValue()).build()))
            .collect(Collectors.toList()) : null)
            .build();
    }
    
    // JPA → Domain (after loading)
    public Order toDomain(OrderJpaEntity entity) {
        Order order = Order.builder()
            .trackingId(entity.getTrackingId() != null ? TrackingId.of(entity.getTrackingId()) : null)
            .customerId(entity.getCustomerId() != null ? CustomerId.of(entity.getCustomerId()) : null)
            .restaurantId(entity.getRestaurantId() != null ? RestaurantId.of(entity.getRestaurantId()) : null)
            .orderStatus(entity.getStatus() != null ? entity.getStatus() : null)
            .price(entity.getTotalAmount() != null ? new Money(entity.getTotalAmount()) : null)
            .items(entity.getItems() != null ? entity.getItems().stream()
            .<OrderItem>map(item -> OrderItemDataAccessMapper.toDomain(item))
            .collect(Collectors.toList()) : null)
            .build();
        order.setId(entity.getId() != null ? OrderId.of(entity.getId()) : null);
        return order;
    }
}