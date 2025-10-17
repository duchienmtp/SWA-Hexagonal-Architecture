package com.swa.order_infrastructure.order_messaging.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.swa.order_domain.event.OrderConfirmationEvent;
import com.swa.order_domain.exception.OrderDomainException;
import com.swa.order_infrastructure.order_messaging.config.RabbitMQProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderRabbitMQPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;

    public void sendOrderConfirmationMessage(OrderConfirmationEvent event) {
        try {
            String exchange = rabbitMQProperties.getExchanges().get("orders-exchange");
            String routingKey = rabbitMQProperties.getRoutingKeys().get("order-created-key");
            
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            System.out.println("Sent message with key '" + routingKey + "': " + event);
        } catch (Exception e) {
            throw new OrderDomainException("Failed to send message: " + e.getMessage());
        }
    }
}