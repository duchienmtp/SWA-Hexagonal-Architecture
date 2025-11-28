package com.swa.order_application.ports.output.event;

import com.swa.order_domain.entity.Customer;
import com.swa.order_domain.entity.Order;

public interface IRabbitMQEventPublisher {
    void sendOrderConfirmationMessage(Order order, Customer customer);
}
