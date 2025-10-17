package com.swa.order_application.ports.output.repository;

import java.util.Optional;

import com.swa.order_domain.entity.Order;
import com.swa.order_domain.valueobject.OrderId;

public interface IOrderRepository {
    // Persist order-agnostic of MySQL, MongoDB, or InMemory
    Order save(Order order);

    // FindbyID
    Optional<Order> findById(OrderId orderId);

    // Cancel order
    Order cancelOrder(OrderId orderId);
}
