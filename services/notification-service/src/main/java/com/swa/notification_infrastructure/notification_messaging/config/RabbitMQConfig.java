package com.swa.notification_infrastructure.notification_messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    private final RabbitMQProperties properties;

    public RabbitMQConfig(RabbitMQProperties properties) {
        this.properties = properties;
    }

    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(properties.getExchanges().get("orders-exchange"));
    }

    // Bean for the first queue
    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder
                .durable(properties.getQueues().get("order-created-queue"))
                .withArgument("x-dead-letter-exchange", properties.getExchanges().get("orders-exchange.dlx"))
                .withArgument("x-dead-letter-routing-key", properties.getRoutingKeys().get("order-created-key.dlx"))
                .ttl(60000)
                .build();
    }

    // Binding for the first queue
    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(orderCreatedQueue)
                .to(ordersExchange)
                .with(properties.getRoutingKeys().get("order-created-key"));
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(properties.getExchanges().get("orders-exchange.dlx"));
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder
                .durable(properties.getQueues().get("order-created-queue.dlq"))
                .build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(properties.getRoutingKeys().get("order-created-key.dlx"));
    }
}
