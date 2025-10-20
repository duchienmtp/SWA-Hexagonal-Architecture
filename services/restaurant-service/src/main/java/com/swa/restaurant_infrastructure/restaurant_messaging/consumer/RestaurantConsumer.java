package com.swa.restaurant_infrastructure.restaurant_messaging.consumer;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.swa.kafka.avro.model.OrderPrepareEventAvro;
import com.swa.kafka.avro.model.RestaurantInventoryRollbackEventAvro;
import com.swa.restaurant_application.ports.input.IRestaurantApplicationService;
import com.swa.restaurant_domain.event.OrderPrepare;
import com.swa.restaurant_domain.event.RestaurantInventoryRollbackEvent;
import com.swa.restaurant_infrastructure.restaurant_messaging.mapper.RestaurantEventMapper;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantConsumer {
    private final IRestaurantApplicationService _restaurantApplicationService;
    private final RestaurantEventMapper restaurantEventMapper;

    @RetryableTopic(attempts = "#{${spring.kafka.listener.common-error-handler.max-attempts}}", backoff = @Backoff(delayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.initial-interval}}", multiplierExpression = "#{${spring.kafka.listener.common-error-handler.back-off.multiplier}}", maxDelayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.max-interval}}"), dltTopicSuffix = "${spring.kafka.listener.dead-letter-publishing.topic-suffix}", topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, autoCreateTopics = "true", kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = "order-purchase-success-topic", groupId = "restaurant-service-group")
    public void consumeOrderPurchaseSuccessEvent(OrderPrepareEventAvro event) {
        try {
            log.info(format("Consuming the Message=[{}] from Topic=[{}]", event, "order-purchase-success-topic"));
            // throw new RuntimeException("Simulated processing failure for testing retry
            // mechanism");

            OrderPrepare orderPrepareEvent = restaurantEventMapper.toOrderPrepareEvent(event);

            _restaurantApplicationService.handleOrderPrepare(orderPrepareEvent);

        } catch (Exception e) {
            log.error("Error processing Message=[{}] from Topic=[{}]", event, "order-purchase-success-topic");
            throw new RuntimeException(e);
        }
    }

    // ## Listener for the DLT topic ##
    @DltHandler
    @KafkaListener(topics = "order-purchase-success-topic.DLT", groupId = "restaurant-service-group")
    public void consumeDltMessage(OrderPrepareEventAvro event) {

        log.error("Received message from DLT topic:: %s", event);
    }

    @RetryableTopic(attempts = "#{${spring.kafka.listener.common-error-handler.max-attempts}}", backoff = @Backoff(delayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.initial-interval}}", multiplierExpression = "#{${spring.kafka.listener.common-error-handler.back-off.multiplier}}", maxDelayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.max-interval}}"), dltTopicSuffix = "${spring.kafka.listener.dead-letter-publishing.topic-suffix}", topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, autoCreateTopics = "true", kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = "restaurant-inventory-rollback-topic", groupId = "restaurant-service-group")
    public void consumerRestaurantInventoryRollbackEvent(RestaurantInventoryRollbackEventAvro event) {
        log.info(format("Consuming the Message=[{}] from Topic=[{}]", event, "restaurant-inventory-rollback-topic"));

        // UserBalance userBalance = null;
        try {
            // throw new RuntimeException("Simulated processing failure for testing retry
            // mechanism");

            RestaurantInventoryRollbackEvent rollbackEvent = restaurantEventMapper.toRestaurantInventoryRollbackEvent(event);

            _restaurantApplicationService.handleInventoryRollback(rollbackEvent);

            log.info("Rollback restaurant inventory successfully for order ID: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error processing Message=[{}] from Topic=[{}]", event, "restaurant-inventory-rollback-topic");
            throw new RuntimeException(e);
        }
    }

    // ## Listener for the DLT topic ##
    @DltHandler
    @KafkaListener(topics = "restaurant-inventory-rollback-topic.DLT", groupId = "restaurant-service-group")
    public void consumerRestaurantInventoryRollbackEventDltMessage(RestaurantInventoryRollbackEventAvro event) {

        log.error("Received message from DLT topic:: %s", event);
    }
}
