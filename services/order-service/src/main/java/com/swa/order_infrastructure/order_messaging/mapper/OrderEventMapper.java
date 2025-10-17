package com.swa.order_infrastructure.order_messaging.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.swa.kafka.avro.model.*;
import com.swa.order_domain.entity.Customer;
import com.swa.order_domain.entity.Order;
import com.swa.order_domain.entity.OrderItem;
import com.swa.order_domain.event.OrderConfirmationEvent;

@Component
public class OrderEventMapper {
    public OrderConfirmationEventAvro mapToOrderConfirmationEvent(Order order, Customer customer) {
        var avroItems = order.getItems().stream()
                .map(this::toOrderItemAvro)
                .collect(Collectors.toList());

        var avroCustomer = toCustomerAvro(customer);

        return OrderConfirmationEventAvro.newBuilder()
                .setOrderId(order.getId().getValue().toString())
                .setTotalAmount(order.getPrice().getAmount().toPlainString())
                .setCustomer(avroCustomer)
                .setItems(avroItems)
                .build();
    }

    private OrderItemAvro toOrderItemAvro(OrderItem orderItem) {
        return OrderItemAvro.newBuilder()
                .setProductId(orderItem.getProductId().getValue().toString())
                .setPrice(orderItem.getPrice().getAmount().toPlainString())
                .setQuantity(orderItem.getQuantity())
                .build();
    }

    private CustomerAvro toCustomerAvro(Customer customer) {
        return CustomerAvro.newBuilder()
                .setId(customer.getId())
                .setFullName(customer.getFullName())
                .setEmail(customer.getEmail())
                .build();
    }

    public OrderConfirmationEvent toOrderConfirmationEvent(Order order, Customer customer) {
        return OrderConfirmationEvent.builder()
                .orderId(order.getId())
                .totalAmount(order.getPrice())
                .customer(customer)
                .items(order.getItems())
                .build();
    }
}
