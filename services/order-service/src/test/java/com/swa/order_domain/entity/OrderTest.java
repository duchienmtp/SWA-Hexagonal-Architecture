package com.swa.order_domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.swa.order_domain.exception.OrderDomainException;
import com.swa.order_domain.valueobject.CustomerId;
import com.swa.order_domain.valueobject.Money;
import com.swa.order_domain.valueobject.OrderId;
import com.swa.order_domain.valueobject.OrderStatus;
import com.swa.order_domain.valueobject.ProductId;
import com.swa.order_domain.valueobject.RestaurantId;
import com.swa.order_domain.valueobject.StreetAddress;
import com.swa.order_domain.valueobject.TrackingId;

@DisplayName("Order aggregate unit tests")
class OrderTest {

    @Test
    @DisplayName("pay switches status from PENDING to PAID")
    void pay_ShouldChangeStatusToPaid() {
        Order order = buildInitializedOrder();

        order.pay();

        assertEquals(OrderStatus.PAID, order.getOrderStatus());
    }

    @Test
    @DisplayName("pay rejects orders that are not pending")
    void pay_ShouldFailWhenNotPending() {
        Order order = buildInitializedOrder();
        order.setOrderStatus(OrderStatus.CANCELLED);

        assertThrows(OrderDomainException.class, order::pay);
    }

    @Test
    @DisplayName("validateOrder fails when item data is invalid")
    void validateOrder_ShouldThrowWhenItemInvalid() {
        Order order = buildInitializedOrder();
        order.getItems().get(0).setQuantity(0);

        assertThrows(OrderDomainException.class, order::validateOrder);
    }

    private Order buildInitializedOrder() {
        Order order = Order.builder()
                .customerId(CustomerId.of(UUID.randomUUID()))
                .restaurantId(RestaurantId.of(UUID.randomUUID()))
                .deliveryAddress(new StreetAddress("123 Main", "10000", "Hanoi"))
                .price(new Money(BigDecimal.valueOf(100)))
                .items(List.of(OrderItem.builder()
                        .productId(ProductId.of(UUID.randomUUID()))
                        .price(new Money(BigDecimal.valueOf(50)))
                        .quantity(2)
                        .subTotal(new Money(BigDecimal.valueOf(100)))
                        .build()))
                .failureMessages(null)
                .build();

        order.setId(OrderId.of(UUID.randomUUID()));
        order.setTrackingId(TrackingId.of(UUID.randomUUID()));
        order.setOrderStatus(OrderStatus.PENDING);
        order.getItems().forEach(item -> item.setOrderId(order.getId()));
        return order;
    }
}
