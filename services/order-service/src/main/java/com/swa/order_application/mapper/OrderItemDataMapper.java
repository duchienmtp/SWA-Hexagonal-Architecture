package com.swa.order_application.mapper;

import org.springframework.stereotype.Component;

import com.swa.order_application.dto.CreateOrderItemDTO;
import com.swa.order_application.dto.OrderItemDTO;
import com.swa.order_domain.entity.*;
import com.swa.order_domain.valueobject.*;

@Component
public class OrderItemDataMapper {
    public OrderItem toOrderItem(CreateOrderItemDTO item) {
        return OrderItem.builder()
            .productId(ProductId.of(item.getProductId()))
            .price(new Money(item.getPrice()))
            .quantity(item.getQuantity())
            .subTotal(new Money(item.getSubTotal()))
            .build();
    }

    public OrderItemDTO toOrderItemDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .productId(item.getProductId().getValue())
                .price(item.getPrice().getAmount())
                .quantity(item.getQuantity())
                .subTotal(item.getSubTotal().getAmount())
                .build();
    }
}
