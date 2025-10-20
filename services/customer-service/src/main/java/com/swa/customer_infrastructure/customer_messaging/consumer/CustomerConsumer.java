package com.swa.customer_infrastructure.customer_messaging.consumer;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.swa.customer_application.ports.input.service.ICustomerService;
import com.swa.kafka.avro.model.CreateUserBalanceFailedEventAvro;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerConsumer {
    private final ICustomerService _customerService;

    @RetryableTopic(attempts = "#{${spring.kafka.listener.common-error-handler.max-attempts}}", backoff = @Backoff(delayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.initial-interval}}", multiplierExpression = "#{${spring.kafka.listener.common-error-handler.back-off.multiplier}}", maxDelayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.max-interval}}"), dltTopicSuffix = "${spring.kafka.listener.dead-letter-publishing.topic-suffix}", topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, autoCreateTopics = "true", kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = "create-user-balance-failed-topic", groupId = "customer-service-group")
    public void consumeUserBalanceCreationFailedEvent(CreateUserBalanceFailedEventAvro event) {
        try {
            log.info(format("Consuming the message from Topic:: %s", event));
            // throw new RuntimeException("Simulated processing failure for testing retry
            // mechanism");

            _customerService.deleteCustomer(event.getCustomerId().toString());

        } catch (Exception e) {
            log.error("Error processing message from Topic:: %s", event);
            throw new RuntimeException(e);
        }
    }

    // ## Listener for the DLT topic ##
    @DltHandler
    @KafkaListener(topics = "create-user-balance-failed-topic.DLT", groupId = "customer-service-group")
    public void consumeDltMessage(CreateUserBalanceFailedEventAvro event) {
        log.error("Received message from DLT topic:: %s", event);
    }
}
