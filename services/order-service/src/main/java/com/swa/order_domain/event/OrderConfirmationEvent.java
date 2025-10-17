package com.swa.order_domain.event;

import java.util.List;

import com.swa.order_domain.entity.Customer;
import com.swa.order_domain.entity.OrderItem;
import com.swa.order_domain.valueobject.Money;
import com.swa.order_domain.valueobject.OrderId;

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
    private Money totalAmount;
    private Customer customer;
    private List<OrderItem> items;
}
