package com.swa.payment_infrastructure.payment_messaging.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.swa.kafka.avro.model.CreateUserBalanceFailedEventAvro;
import com.swa.kafka.avro.model.CustomerAvro;
import com.swa.kafka.avro.model.OrderConfirmationEventAvro;
import com.swa.kafka.avro.model.OrderItemAvro;
import com.swa.kafka.avro.model.OrderPrepareEventAvro;
import com.swa.kafka.avro.model.ProcessPaymentFailedEventAvro;
import com.swa.payment_domain.entity.Customer;
import com.swa.payment_domain.entity.OrderItem;
import com.swa.payment_domain.entity.UserBalance;
import com.swa.payment_domain.event.OrderConfirmationEvent;
import com.swa.payment_domain.event.PaymentEvent;
import com.swa.payment_domain.event.ProcessPaymentFailedEvent;
import com.swa.payment_domain.valueobject.CustomerId;
import com.swa.payment_domain.valueobject.Money;
import com.swa.payment_domain.valueobject.OrderId;
import com.swa.payment_domain.valueobject.ProductId;
import com.swa.payment_domain.valueobject.RestaurantId;

@Component
public class PaymentEventMapper {
    public OrderConfirmationEvent toOrderConfirmationEvent(OrderConfirmationEventAvro orderConfirmationEventAvro) {
        return OrderConfirmationEvent.builder()
                .orderId(OrderId.toOrderId(orderConfirmationEventAvro.getOrderId()))
                .restaurantId(RestaurantId.toRestaurantId(orderConfirmationEventAvro.getRestaurantId()))
                .totalAmount(new Money(new BigDecimal(orderConfirmationEventAvro.getTotalAmount())))
                .customer(toCustomer(orderConfirmationEventAvro.getCustomer()))
                .items(orderConfirmationEventAvro.getItems().stream()
                        .map(this::toOrderItem)
                        .toList())
                .build();
    }

    public Customer toCustomer(CustomerAvro customerAvro) {
        return Customer.builder()
                .id(customerAvro.getId())
                .fullName(customerAvro.getFullName())
                .email(customerAvro.getEmail())
                .build();
    }

    public OrderItem toOrderItem(OrderItemAvro orderItemAvro) {
        return OrderItem.builder()
                .productId(ProductId.toProductId(orderItemAvro.getProductId()))
                .price(new Money(new BigDecimal(orderItemAvro.getPrice())))
                .quantity(orderItemAvro.getQuantity())
                .build();
    }

    public OrderItemAvro toOrderItemAvro(OrderItem orderItem) {
        return OrderItemAvro.newBuilder()
                .setProductId(orderItem.getProductId().getValue().toString())
                .setPrice(orderItem.getPrice().getAmount().toString())
                .setQuantity(orderItem.getQuantity())
                .build();
    }

    public OrderPrepareEventAvro toOrderPrepareEventAvro(OrderConfirmationEvent orderConfirmationEvent,
            PaymentEvent paymentEvent) {
        var avroItems = orderConfirmationEvent.getItems().stream()
                .map(this::toOrderItemAvro)
                .toList();

        return OrderPrepareEventAvro.newBuilder()
                .setOrderId(orderConfirmationEvent.getOrderId().getValue().toString())
                .setRestaurantId(orderConfirmationEvent.getRestaurantId().getValue().toString())
                .setCustomerId(orderConfirmationEvent.getCustomer().getId())
                .setItems(avroItems)
                .build();
    }

    public CreateUserBalanceFailedEventAvro toCreateUserBalanceFailedEventAvro(UserBalance userBalance) {
        return CreateUserBalanceFailedEventAvro.newBuilder()
                .setCustomerId(userBalance.getCustomerId().getValue().toString())
                .setBalance(userBalance.getBalance().getAmount().toString())
                .setMessage("Failed to create user balance")
                .build();
    }

    public ProcessPaymentFailedEventAvro toProcessPaymentFailedEventAvro(
            ProcessPaymentFailedEvent event,
            PaymentEvent paymentEvent) {
        return ProcessPaymentFailedEventAvro.newBuilder()
                .setOrderId(event.getOrderId().getValue().toString())
                .setCustomerId(event.getCustomerId().getValue().toString())
                .setMessage(paymentEvent.getMessage())
                .build();
    }

    public ProcessPaymentFailedEventAvro toProcessPaymentFailedEventAvro(
            OrderConfirmationEvent event,
            PaymentEvent paymentEvent) {
        return ProcessPaymentFailedEventAvro.newBuilder()
                .setOrderId(event.getOrderId().getValue().toString())
                .setCustomerId(event.getCustomer().getId().toString())
                .setMessage(paymentEvent.getMessage())
                .build();
    }

    public ProcessPaymentFailedEvent toProcessPaymentFailedEvent(ProcessPaymentFailedEventAvro eventAvro) {
        return ProcessPaymentFailedEvent.builder()
                .orderId(OrderId.toOrderId(eventAvro.getOrderId()))
                .customerId(CustomerId.toCustomerId(eventAvro.getCustomerId()))
                .message(eventAvro.getMessage())
                .build();
    }
}
