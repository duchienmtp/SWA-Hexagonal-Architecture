package com.swa.restaurant_domain.event;

import java.util.List;

import com.swa.restaurant_domain.entity.OrderItem;
import com.swa.restaurant_domain.valueobject.CustomerId;
import com.swa.restaurant_domain.valueobject.OrderId;
import com.swa.restaurant_domain.valueobject.RestaurantId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderPrepare {
    private OrderId orderId;
    private CustomerId customerId;
    private RestaurantId restaurantId;
    private List<OrderItem> orderItems;
}
