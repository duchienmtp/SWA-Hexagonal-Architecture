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
import com.swa.payment_domain.valueobject.Money;
import com.swa.payment_domain.valueobject.OrderId;
import com.swa.payment_domain.valueobject.ProductId;

@Component
public class PaymentEventMapper {
    public OrderConfirmationEvent toOrderConfirmationEvent(OrderConfirmationEventAvro orderConfirmationEventAvro) {
        return OrderConfirmationEvent.builder()
                .orderId(OrderId.toOrderId(orderConfirmationEventAvro.getOrderId()))
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

    public OrderConfirmationEventAvro toOrderConfirmationEventAvro(OrderConfirmationEvent orderConfirmationEvent) {
        var avroItems = orderConfirmationEvent.getItems().stream()
                .map(item -> OrderItemAvro.newBuilder()
                        .setProductId(item.getProductId().getValue().toString())
                        .setPrice(item.getPrice().getAmount().toString())
                        .setQuantity(item.getQuantity())
                        .build())
                .toList();

        CustomerAvro customerAvro = CustomerAvro.newBuilder()
                .setId(orderConfirmationEvent.getCustomer().getId())
                .setFullName(orderConfirmationEvent.getCustomer().getFullName())
                .setEmail(orderConfirmationEvent.getCustomer().getEmail())
                .build();

        return OrderConfirmationEventAvro.newBuilder()
                .setOrderId(orderConfirmationEvent.getOrderId().getValue().toString())
                .setTotalAmount(orderConfirmationEvent.getTotalAmount().getAmount().toString())
                .setCustomer(customerAvro)
                .setItems(avroItems)
                .build();
    }

    public OrderPrepareEventAvro toOrderPrepareEventAvro(OrderConfirmationEventAvro orderConfirmationEvent,
            PaymentEvent paymentEvent) {
        var avroItems = orderConfirmationEvent.getItems();

        return OrderPrepareEventAvro.newBuilder()
                .setOrderId(orderConfirmationEvent.getOrderId().toString())
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
}
