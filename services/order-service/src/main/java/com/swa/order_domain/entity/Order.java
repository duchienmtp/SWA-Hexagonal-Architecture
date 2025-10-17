package com.swa.order_domain.entity;

import java.util.List;
import java.util.UUID;

import com.swa.order_domain.exception.OrderDomainException;
import com.swa.order_domain.valueobject.CustomerId;
import com.swa.order_domain.valueobject.Money;
import com.swa.order_domain.valueobject.OrderId;
import com.swa.order_domain.valueobject.OrderStatus;
import com.swa.order_domain.valueobject.RestaurantId;
import com.swa.order_domain.valueobject.StreetAddress;
import com.swa.order_domain.valueobject.TrackingId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
public class Order extends AggregateRoot<OrderId> {
    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final StreetAddress deliveryAddress;
    private final Money price;
    private final List<OrderItem> items;

    // Mutable fields - can be changed via business logic
    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessages;

    // Business logic methods
    public void validateOrder() {
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }

    private void validateInitialOrder() {
        if (orderStatus == null || getId() == null || trackingId == null) {
            throw new OrderDomainException("Order is not in correct state for initialization!");
        }
    }

    private void validateTotalPrice() {
        if (price == null || !price.isGreaterThanZero()) {
            throw new OrderDomainException("Total price must be greater than zero!");
        }
    }

    private void validateItemsPrice() {
        // Calculate total from items
        Money orderItemsTotal = items.stream().map(orderItem -> {
            orderItem.validateItem();
            return orderItem.getSubTotal();
        })
                .reduce(Money.ZERO, Money::add);

        // Compare with order price
        if (price.getAmount().compareTo(orderItemsTotal.getAmount()) != 0) {
            throw new OrderDomainException(
                    "Total price: " + price.getAmount() +
                            " is not equal to Order items total: " +
                            orderItemsTotal.getAmount() + "!");
        }
    }

    private void initializeOrderItems() {
        for (OrderItem item : items) {
            item.setOrderId(getId());
            item.setSubTotal(item.calculateSubtotal());
        }
    }

    public void pay() {
        if (orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException("Order is not in correct state for pay operation!");
        }
        orderStatus = OrderStatus.PAID;
    }
}
