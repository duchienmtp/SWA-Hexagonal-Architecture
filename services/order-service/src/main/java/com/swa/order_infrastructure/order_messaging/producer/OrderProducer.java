package com.swa.order_infrastructure.order_messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.swa.kafka.avro.model.OrderConfirmationEventAvro;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {
    private final KafkaTemplate<String, OrderConfirmationEventAvro> kafkaTemplate;

    public void sendOrderConfirmation(OrderConfirmationEventAvro orderConfirmationEvent) {
        try {
            kafkaTemplate.send("order-topic", orderConfirmationEvent).whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent message=[{}] with offset=[{}]",
                            orderConfirmationEvent,
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Unable to send message=[{}] due to: {}",
                            orderConfirmationEvent,
                            ex.getMessage(),
                            ex);
                }
            });

        } catch (Exception e) {
            log.error("Failed to send message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }
}
