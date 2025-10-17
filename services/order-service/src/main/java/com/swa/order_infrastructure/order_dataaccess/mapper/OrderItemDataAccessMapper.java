package com.swa.order_infrastructure.order_dataaccess.mapper;

import com.swa.order_domain.entity.OrderItem;
import com.swa.order_domain.valueobject.Money;
import com.swa.order_domain.valueobject.OrderId;
import com.swa.order_domain.valueobject.ProductId;
import com.swa.order_infrastructure.order_dataaccess.entity.OrderItemJpaEntity;
import com.swa.order_infrastructure.order_dataaccess.entity.OrderJpaEntity;

public class OrderItemDataAccessMapper {
    
    public static OrderItemJpaEntity toJpaEntity(OrderItem item, OrderJpaEntity order) {
        return OrderItemJpaEntity.builder()
            .order(order)
            .productId(item.getProductId().getValue())
            .price(item.getPrice().getAmount())
            .quantity(item.getQuantity())
            .build();
    }
    
    public static OrderItem toDomain(OrderItemJpaEntity item) {
        return OrderItem.builder()
            .orderId(OrderId.of(item.getOrder().getId()))
            .productId(ProductId.of(item.getProductId()))
            .price(new Money(item.getPrice()))
            .quantity(item.getQuantity())
            .subTotal(new Money(item.getPrice()).multiply(item.getQuantity()))
            .build();
    }
}
