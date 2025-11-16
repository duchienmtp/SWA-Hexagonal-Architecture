package com.swa.order_domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.swa.order_domain.entity.Order;
import com.swa.order_domain.entity.OrderItem;
import com.swa.order_domain.exception.OrderDomainException;
import com.swa.order_domain.valueobject.CustomerId;
import com.swa.order_domain.valueobject.Money;
import com.swa.order_domain.valueobject.OrderStatus;
import com.swa.order_domain.valueobject.ProductId;
import com.swa.order_domain.valueobject.RestaurantId;
import com.swa.order_domain.valueobject.StreetAddress;

@DisplayName("OrderDomainService unit tests")
class OrderDomainServiceTest {

    private final OrderDomainService orderDomainService = new OrderDomainService();

    @Test
    @DisplayName("validateAndInitializeOrder sets identifiers and status")
    void validateAndInitializeOrder_ShouldInitializeOrderAggregate() {
        Order order = buildOrder(BigDecimal.valueOf(200));

        Order result = orderDomainService.validateAndInitializeOrder(order);

        assertSame(order, result);
        assertNotNull(order.getId());
        assertNotNull(order.getTrackingId());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        // Validate each item's orderId and subtotal calculation
        order.getItems().forEach(item -> {
            assertEquals(order.getId(), item.getOrderId());
            assertEquals(0, item.getSubTotal().getAmount()
                    .compareTo(item.getPrice().getAmount().multiply(BigDecimal.valueOf(item.getQuantity()))));
        });
    }

    @Test
    @DisplayName("validateAndInitializeOrder rejects mismatched totals")
    void validateAndInitializeOrder_ShouldFailWhenTotalsMismatch() {
        Order order = buildOrder(BigDecimal.valueOf(150));
        // The items total is 200, but we set order total to 150 to trigger the failure
        assertThrows(OrderDomainException.class,
                () -> orderDomainService.validateAndInitializeOrder(order));
    }

    private Order buildOrder(BigDecimal totalPrice) {
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        OrderItem firstItem = OrderItem.builder()
                .productId(ProductId.of(UUID.randomUUID()))
                .price(new Money(BigDecimal.valueOf(50)))
                .quantity(2)
                .subTotal(new Money(BigDecimal.valueOf(100)))
                .build();
        OrderItem secondItem = OrderItem.builder()
                .productId(ProductId.of(UUID.randomUUID()))
                .price(new Money(BigDecimal.valueOf(50)))
                .quantity(2)
                .subTotal(new Money(BigDecimal.valueOf(100)))
                .build();

        return Order.builder()
                .customerId(CustomerId.of(customerId))
                .restaurantId(RestaurantId.of(restaurantId))
                .deliveryAddress(new StreetAddress("123 Main", "10000", "Hanoi"))
                .price(new Money(totalPrice))
                .items(List.of(firstItem, secondItem))
                .failureMessages(null)
                .build();
    }
}
