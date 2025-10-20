package com.swa.order_infrastructure.order_messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.swa.kafka.avro.model.*;
import com.swa.order_application.ports.output.event.IEventPublisher;
import com.swa.order_domain.entity.Customer;
import com.swa.order_domain.entity.Order;
import com.swa.order_domain.valueobject.CustomerId;
import com.swa.order_infrastructure.order_messaging.mapper.OrderEventMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer implements IEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderEventMapper orderEventMapper;

    public void publish(String topic, Object payload) {
        try {
            kafkaTemplate.send(topic, payload).whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent message=[{}] to topic=[{}]",
                            payload,
                            topic);
                } else {
                    log.error("Unable to send message=[{}] due to: {}",
                            payload,
                            ex.getMessage(),
                            ex);
                }
            });

        } catch (Exception e) {
            log.error("Failed to send message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }

    public void sendOrderConfirmationEvent(Order order, Customer customer) {
        OrderConfirmationEventAvro orderConfirmationEvent = orderEventMapper.mapToOrderConfirmationEvent(order,
                customer);
        publish("order-confirmation-topic", orderConfirmationEvent);
    }

    public void sendOrderPurchaseEvent(Order order, Customer customer) {
        OrderConfirmationEventAvro orderPurchaseEvent = orderEventMapper.mapToOrderConfirmationEvent(order, customer);
        publish("order-purchase-topic", orderPurchaseEvent);
    }

    public void sendRestaurantInventoryRollbackEvent(Order order, CustomerId customerId, String message) {
        RestaurantInventoryRollbackEventAvro inventoryRollbackEvent = orderEventMapper
                .mapToRestaurantInventoryRollbackEvent(order, customerId, message);
        publish("restaurant-inventory-rollback-topic", inventoryRollbackEvent);
    }
}
