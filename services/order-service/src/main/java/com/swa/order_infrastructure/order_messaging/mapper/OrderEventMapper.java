package com.swa.order_infrastructure.order_messaging.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.swa.kafka.avro.model.*;
import com.swa.order_domain.entity.Customer;
import com.swa.order_domain.entity.Order;
import com.swa.order_domain.entity.OrderItem;
import com.swa.order_domain.event.OrderApprovalEvent;
import com.swa.order_domain.event.OrderConfirmationEvent;
import com.swa.order_domain.event.ProcessPaymentFailedEvent;
import com.swa.order_domain.valueobject.CustomerId;
import com.swa.order_domain.valueobject.OrderId;

@Component
public class OrderEventMapper {
    public OrderConfirmationEventAvro mapToOrderConfirmationEvent(Order order, Customer customer) {
        var avroItems = order.getItems().stream()
                .map(this::toOrderItemAvro)
                .collect(Collectors.toList());

        var avroCustomer = toCustomerAvro(customer);

        return OrderConfirmationEventAvro.newBuilder()
                .setOrderId(order.getId().getValue().toString())
                .setRestaurantId(order.getRestaurantId().getValue().toString())
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

    public ProcessPaymentFailedEvent toProcessPaymentFailedEvent(ProcessPaymentFailedEventAvro avro) {
        return ProcessPaymentFailedEvent.builder()
                .orderId(OrderId.toOrderId(avro.getOrderId()))
                .customerId(CustomerId.toCustomerId(avro.getCustomerId()))
                .message(avro.getMessage())
                .build();
    }

    public OrderApprovalEvent toOrderApprovalEvent(OrderApprovalEventAvro avro) {
        return OrderApprovalEvent.builder()
                .orderId(OrderId.toOrderId(avro.getOrderId()))
                .customerId(CustomerId.toCustomerId(avro.getCustomerId()))
                .message(avro.getMessage())
                .build();
    }

    public RestaurantInventoryRollbackEventAvro mapToRestaurantInventoryRollbackEvent(Order order, CustomerId customerId, String message) {
        var avroItems = order.getItems().stream()
                .map(this::toOrderItemAvro)
                .collect(Collectors.toList());

        return RestaurantInventoryRollbackEventAvro.newBuilder()
                .setOrderId(order.getId().getValue().toString())
                .setRestaurantId(order.getRestaurantId().getValue().toString())
                .setCustomerId(customerId.getValue().toString())
                .setMessage(message)
                .setItems(avroItems)
                .build();
    }
}
