package com.swa.notification_infrastructure.notification_messaging.consumer;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;

import com.swa.notification_application.ports.input.INotificationApplicationService;
import com.swa.notification_domain.event.OrderConfirmationEvent;
import com.swa.notification_infrastructure.notification_messaging.mapper.NotificationEventMapper;
import com.swa.kafka.avro.model.OrderConfirmationEventAvro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
    private final INotificationApplicationService _notificationApplicationService;
    private final NotificationEventMapper notificationEventMapper;

    @RetryableTopic(attempts = "#{${spring.kafka.listener.common-error-handler.max-attempts}}", backoff = @Backoff(delayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.initial-interval}}", multiplierExpression = "#{${spring.kafka.listener.common-error-handler.back-off.multiplier}}", maxDelayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.max-interval}}"), dltTopicSuffix = "${spring.kafka.listener.dead-letter-publishing.topic-suffix}", topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, autoCreateTopics = "true", kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = "order-confirmation-topic", groupId = "orderNotificationGroup")
    public void consumeOrderConfirmationNotifications(OrderConfirmationEventAvro orderConfirmationEvent) {
        try {
            log.info(format("Consuming the message from Topic:: %s", orderConfirmationEvent));
            // throw new RuntimeException("Simulated processing failure for testing retry
            // mechanism");
            OrderConfirmationEvent event = notificationEventMapper.toOrderConfirmationEvent(orderConfirmationEvent);
            _notificationApplicationService.sendOrderConfirmationNotification(event);
        } catch (Exception e) {
            log.error("Error processing message from Topic:: %s", orderConfirmationEvent);
            throw new RuntimeException(e);
        }
    }

    // ## Listener for the DLT topic ##
    @DltHandler
    @KafkaListener(topics = "order-confirmation-topic.DLT", groupId = "orderNotificationDltGroup")
    public void consumeDltMessage(OrderConfirmationEventAvro orderConfirmationEvent) {

        log.error("Received message from DLT topic:: %s", orderConfirmationEvent);
    }
}
