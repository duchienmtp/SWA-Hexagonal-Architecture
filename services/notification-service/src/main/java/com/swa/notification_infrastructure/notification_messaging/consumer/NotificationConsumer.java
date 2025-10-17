package com.swa.notification_infrastructure.notification_messaging.consumer;

import java.util.UUID;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;

import com.swa.notification_application.ports.output.service.IEmailService;
import com.swa.notification_domain.entity.OrderItem;
import com.swa.kafka.avro.model.OrderConfirmationEventAvro;
import com.swa.notification_domain.valueobject.Money;
import com.swa.notification_domain.valueobject.OrderId;
import com.swa.notification_domain.valueobject.ProductId;
import com.swa.notification_infrastructure.notification_dataaccess.repository.INotificationRepository;
import com.swa.notification_infrastructure.notification_messaging.mapper.NotificationEventMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
    private final INotificationRepository _notificationRepository;
    private final IEmailService _emailService;
    private final NotificationEventMapper notificationEventMapper;

    @RetryableTopic(attempts = "#{${spring.kafka.listener.common-error-handler.max-attempts}}", backoff = @Backoff(delayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.initial-interval}}", multiplierExpression = "#{${spring.kafka.listener.common-error-handler.back-off.multiplier}}", maxDelayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.max-interval}}"), dltTopicSuffix = "${spring.kafka.listener.dead-letter-publishing.topic-suffix}", topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, autoCreateTopics = "true", kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = "order-topic", groupId = "orderNotificationGroup")
    public void consumeOrderConfirmationNotifications(OrderConfirmationEventAvro orderConfirmationEvent) {
        try {
            log.info(format("Consuming the message from order-topic Topic:: %s", orderConfirmationEvent));
            // throw new RuntimeException("Simulated processing failure for testing retry
            // mechanism");
            var notification = notificationEventMapper.toNotificationEntity(orderConfirmationEvent);
            _notificationRepository.save(notification);

            var customerName = orderConfirmationEvent.getCustomer().getFullName();
            _emailService.sendOrderConfirmationEmail(
                    orderConfirmationEvent.getCustomer().getEmail(),
                    customerName,
                    new BigDecimal(orderConfirmationEvent.getTotalAmount()),
                    OrderId.of(UUID.fromString(orderConfirmationEvent.getOrderId())),
                    orderConfirmationEvent.getItems().stream().map(
                            item -> OrderItem.builder()
                                    .productId(ProductId.of(UUID.fromString(item.getProductId())))
                                    .price(new Money(new BigDecimal(item.getPrice())))
                                    .quantity(item.getQuantity())
                                    .build())
                            .toList());
        } catch (Exception e) {
            log.error("Error processing message from order-topic Topic:: %s", orderConfirmationEvent);
            throw new RuntimeException(e);
        }
    }

    // ## Listener for the DLT topic ##
    @DltHandler
    @KafkaListener(topics = "order-topic.DLT", groupId = "orderNotificationDltGroup")
    public void consumeDltMessage(OrderConfirmationEventAvro orderConfirmationEvent) {

        log.error("Received message from DLT topic:: %s", orderConfirmationEvent);
    }
}
