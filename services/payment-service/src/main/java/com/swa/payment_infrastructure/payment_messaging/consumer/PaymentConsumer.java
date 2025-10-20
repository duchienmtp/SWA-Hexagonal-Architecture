package com.swa.payment_infrastructure.payment_messaging.consumer;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;

import com.swa.kafka.avro.model.CreateUserBalanceEventAvro;
import com.swa.kafka.avro.model.OrderConfirmationEventAvro;

import com.swa.payment_application.ports.input.IPaymentApplicationService;

import com.swa.payment_domain.entity.UserBalance;
import com.swa.payment_domain.event.OrderConfirmationEvent;
import com.swa.payment_domain.valueobject.CustomerId;
import com.swa.payment_domain.valueobject.Money;
import com.swa.payment_infrastructure.payment_messaging.mapper.PaymentEventMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import static java.lang.String.format;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentConsumer {
    private final IPaymentApplicationService _paymentApplicationService;
    private final PaymentEventMapper paymentEventMapper;

    @RetryableTopic(attempts = "#{${spring.kafka.listener.common-error-handler.max-attempts}}", backoff = @Backoff(delayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.initial-interval}}", multiplierExpression = "#{${spring.kafka.listener.common-error-handler.back-off.multiplier}}", maxDelayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.max-interval}}"), dltTopicSuffix = "${spring.kafka.listener.dead-letter-publishing.topic-suffix}", topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, autoCreateTopics = "true", kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = "order-purchase-topic", groupId = "payment-service-group")
    public void consumeOrderConfirmationNotifications(OrderConfirmationEventAvro event) {
        try {
            log.info(format("Consuming the message from order-purchase-topic Topic:: %s", event));
            // throw new RuntimeException("Simulated processing failure for testing retry
            // mechanism");

            OrderConfirmationEvent orderConfirmationEvent = paymentEventMapper.toOrderConfirmationEvent(event);

            _paymentApplicationService.handleOrderConfirmation(orderConfirmationEvent);

            // var customerName = event.getCustomer().getFullName();
            // _emailService.sendOrderConfirmationEmail(
            // orderPurchaseEvent.getCustomer().getEmail(),
            // customerName,
            // new BigDecimal(orderPurchaseEvent.getTotalAmount()),
            // OrderId.of(UUID.fromString(orderPurchaseEvent.getOrderId())));
        } catch (Exception e) {
            log.error("Error processing message from order-purchase-topic Topic:: %s", event);
            throw new RuntimeException(e);
        }
    }

    // ## Listener for the DLT topic ##
    @DltHandler
    @KafkaListener(topics = "order-purchase-topic.DLT", groupId = "payment-service-group")
    public void consumeDltMessage(OrderConfirmationEventAvro event) {

        log.error("Received message from DLT topic:: %s", event);
    }

    @RetryableTopic(attempts = "#{${spring.kafka.listener.common-error-handler.max-attempts}}", backoff = @Backoff(delayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.initial-interval}}", multiplierExpression = "#{${spring.kafka.listener.common-error-handler.back-off.multiplier}}", maxDelayExpression = "#{${spring.kafka.listener.common-error-handler.back-off.max-interval}}"), dltTopicSuffix = "${spring.kafka.listener.dead-letter-publishing.topic-suffix}", topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, autoCreateTopics = "true", kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = "create-user-balance-topic", groupId = "payment-service-group")
    public void consumerCreateUserBalance(CreateUserBalanceEventAvro event) {
        log.info(format("Consuming the message from Topic:: %s", event));

        UserBalance userBalance = null;
        try {
            // throw new RuntimeException("Simulated processing failure for testing retry
            // mechanism");

            CustomerId customerId = CustomerId.toCustomerId(event.getCustomerId());
            Money initialBalance = new Money(new BigDecimal(event.getBalance()));

            userBalance = new UserBalance(customerId, initialBalance);

            _paymentApplicationService.createUserBalance(userBalance);

            log.info("User balance created successfully for customer ID: {}", event.getCustomerId());
        } catch (Exception e) {
            log.error("Error processing message from Topic:: %s", event);
            throw new RuntimeException(e);
        }
    }

    // ## Listener for the DLT topic ##
    @DltHandler
    @KafkaListener(topics = "create-user-balance-topic.DLT", groupId = "payment-service-group")
    public void consumeCreateUserBalanceDltMessage(CreateUserBalanceEventAvro event) {

        log.error("Received message from DLT topic:: %s", event);
    }
}
