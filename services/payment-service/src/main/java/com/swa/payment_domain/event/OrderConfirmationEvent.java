package com.swa.payment_domain.event;

import java.util.List;

import com.swa.payment_domain.entity.Customer;
import com.swa.payment_domain.entity.OrderItem;
import com.swa.payment_domain.valueobject.Money;
import com.swa.payment_domain.valueobject.OrderId;
import com.swa.payment_domain.valueobject.RestaurantId;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmationEvent {
    private OrderId orderId;
    private RestaurantId restaurantId;
    private Money totalAmount;
    private Customer customer;
    private List<OrderItem> items;
}
