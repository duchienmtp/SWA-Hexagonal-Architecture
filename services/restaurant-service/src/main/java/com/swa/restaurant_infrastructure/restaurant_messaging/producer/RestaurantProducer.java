package com.swa.restaurant_infrastructure.restaurant_messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.swa.kafka.avro.model.OrderApprovalEventAvro;
import com.swa.kafka.avro.model.ProcessPaymentFailedEventAvro;
import com.swa.restaurant_application.ports.output.IEventPublisher;
import com.swa.restaurant_domain.event.OrderPrepare;
import com.swa.restaurant_domain.event.OrderPrepareEvent;
import com.swa.restaurant_domain.event.RestaurantInventoryRollbackEvent;
import com.swa.restaurant_infrastructure.restaurant_messaging.mapper.RestaurantEventMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantProducer implements IEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RestaurantEventMapper restaurantEventMapper;

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

    @Override
    public void publishPrepareOrderSuccess(OrderPrepare order, OrderPrepareEvent event) {
        String topic = event.getOrderPrepareStatus().getTopic();
        OrderApprovalEventAvro orderPrepareEventAvro = restaurantEventMapper
                .toOrderApprovalEventAvro(event, order);

        log.info("Payment processed successfully for order: {}", orderPrepareEventAvro.getOrderId());
        publish(topic, orderPrepareEventAvro);
    }

    @Override
    public void publishPrepareOrderFailure(OrderPrepare order, OrderPrepareEvent event) {
        String topic = event.getOrderPrepareStatus().getTopic();
        ProcessPaymentFailedEventAvro failedEventAvro = restaurantEventMapper
                .toProcessPaymentFailedEventAvro(event, order);

        log.error("Payment failed for order: {}", failedEventAvro.getOrderId());
        publish(topic, failedEventAvro);
    }

    @Override
    public void publishPrepareOrderFailure(RestaurantInventoryRollbackEvent order, OrderPrepareEvent event) {
        String topic = event.getOrderPrepareStatus().getTopic();
        ProcessPaymentFailedEventAvro failedEventAvro = restaurantEventMapper
                .toProcessPaymentFailedEventAvro(event, order);

        log.error("Payment failed for order: {}", failedEventAvro.getOrderId());
        publish(topic, failedEventAvro);
    }
}
