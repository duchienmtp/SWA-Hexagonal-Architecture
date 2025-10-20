package com.swa.order_infrastructure.order_messaging.consumer;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.swa.kafka.avro.model.OrderApprovalEventAvro;
import com.swa.kafka.avro.model.ProcessPaymentFailedEventAvro;
import com.swa.order_application.ports.input.service.IOrderApplicationService;
import com.swa.order_domain.event.OrderApprovalEvent;
import com.swa.order_domain.event.ProcessPaymentFailedEvent;
import com.swa.order_infrastructure.order_messaging.mapper.OrderEventMapper;


@Service
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {
    private final OrderEventMapper orderEventMapper;
    private final IOrderApplicationService _orderApplicationService;

    @RetryableTopic(attempts = "#{${spring.kafka.listener.common-error-handler.max-attempts}}", backoff = @Backoff(delayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.initial-interval}}", multiplierExpression = "#{${spring.kafka.listener.common-error-handler.back-off.multiplier}}", maxDelayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.max-interval}}"), dltTopicSuffix = "${spring.kafka.listener.dead-letter-publishing.topic-suffix}", topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, autoCreateTopics = "true", kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = "order-purchase-failed-topic", groupId = "order-service-group")
    public void consumeProcessPaymentFailedEvent(ProcessPaymentFailedEventAvro paymentFailedEventAvro) {
        try {
            log.info("Consuming the Message=[{}] from Topic=[{}]", paymentFailedEventAvro, "order-purchase-failed-topic");
            // throw new RuntimeException("Simulated processing failure for testing retry
            // mechanism");
            ProcessPaymentFailedEvent event = orderEventMapper.toProcessPaymentFailedEvent(paymentFailedEventAvro);
           _orderApplicationService.cancelOrder(event);
           log.info("Cancelled order with ID: %s", event.getOrderId());
        } catch (Exception e) {
            log.error("Error processing Message=[{}] from Topic=[{}]", paymentFailedEventAvro, "order-purchase-failed-topic");
            throw new RuntimeException(e);
        }
    }

    // ## Listener for the DLT topic ##
    @DltHandler
    @KafkaListener(topics = "order-purchase-failed-topic.DLT", groupId = "orderServiceGroupDltGroup")
    public void consumeProcessPaymentFailedEventDltMessage(ProcessPaymentFailedEventAvro paymentFailedEventAvro) {

        log.error("Received message from DLT topic:: %s", paymentFailedEventAvro);
    }

    @RetryableTopic(attempts = "#{${spring.kafka.listener.common-error-handler.max-attempts}}", backoff = @Backoff(delayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.initial-interval}}", multiplierExpression = "#{${spring.kafka.listener.common-error-handler.back-off.multiplier}}", maxDelayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.max-interval}}"), dltTopicSuffix = "${spring.kafka.listener.dead-letter-publishing.topic-suffix}", topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, autoCreateTopics = "true", kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = "order-prepare-success-topic", groupId = "order-service-group")
    public void consumeOrderPrepareSuccessEvent(OrderApprovalEventAvro orderApprovalEventAvro) {
        try {
            log.info("Consuming the Message=[{}] from Topic=[{}]", orderApprovalEventAvro, "order-prepare-success-topic");
            // throw new RuntimeException("Simulated processing failure for testing retry
            // mechanism");
            OrderApprovalEvent event = orderEventMapper.toOrderApprovalEvent(orderApprovalEventAvro);
           _orderApplicationService.approveOrder(event);
           log.info("Cancelled order with ID: %s", event.getOrderId());
        } catch (Exception e) {
            log.error("Error processing Message=[{}] from Topic=[{}]", orderApprovalEventAvro, "order-prepare-success-topic");
            throw new RuntimeException(e);
        }
    }

    @DltHandler
    @KafkaListener(topics = "order-prepare-success-topic.DLT", groupId = "orderServiceGroupDltGroup")
    public void consumeOrderPrepareSuccessEventDltMessage(OrderApprovalEventAvro orderApprovalEventAvro) {

        log.error("Received message from DLT topic:: %s", orderApprovalEventAvro);
    }
}
