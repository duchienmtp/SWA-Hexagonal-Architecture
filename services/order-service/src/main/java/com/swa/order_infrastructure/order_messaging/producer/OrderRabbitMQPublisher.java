package com.swa.order_infrastructure.order_messaging.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.swa.order_application.ports.output.event.IRabbitMQEventPublisher;
import com.swa.order_domain.entity.Customer;
import com.swa.order_domain.entity.Order;
import com.swa.order_domain.event.OrderConfirmationEvent;
import com.swa.order_domain.exception.OrderDomainException;
import com.swa.order_infrastructure.order_messaging.config.RabbitMQProperties;
import com.swa.order_infrastructure.order_messaging.mapper.OrderEventMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderRabbitMQPublisher implements IRabbitMQEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;
    private final OrderEventMapper orderEventMapper;

    public void sendOrderConfirmationMessage(Order order, Customer customer) {
        try {
            String exchange = rabbitMQProperties.getExchanges().get("orders-exchange");
            String routingKey = rabbitMQProperties.getRoutingKeys().get("order-created-key");

            OrderConfirmationEvent event = orderEventMapper.toOrderConfirmationEvent(order, customer);
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            System.out.println("Sent message with key '" + routingKey + "': " + event);
        } catch (Exception e) {
            throw new OrderDomainException("Failed to send message: " + e.getMessage());
        }
    }
}