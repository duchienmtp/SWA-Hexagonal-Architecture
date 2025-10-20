package com.swa.order_application.ports.output.event;

import com.swa.order_domain.entity.Customer;
import com.swa.order_domain.entity.Order;
import com.swa.order_domain.valueobject.CustomerId;

public interface IEventPublisher {
    void sendOrderConfirmationEvent(Order order, Customer customer);
    void sendOrderPurchaseEvent(Order order, Customer customer);
    void sendRestaurantInventoryRollbackEvent(Order order, CustomerId customerId, String message);
}
