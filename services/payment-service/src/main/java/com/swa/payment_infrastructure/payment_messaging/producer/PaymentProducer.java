package com.swa.payment_infrastructure.payment_messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.swa.kafka.avro.model.CreateUserBalanceFailedEventAvro;
import com.swa.kafka.avro.model.OrderConfirmationEventAvro;
import com.swa.kafka.avro.model.OrderPrepareEventAvro;
import com.swa.kafka.avro.model.ProcessPaymentFailedEventAvro;
import com.swa.payment_application.ports.output.IEventPublisher;
import com.swa.payment_domain.entity.UserBalance;
import com.swa.payment_domain.event.OrderConfirmationEvent;
import com.swa.payment_domain.event.PaymentEvent;
import com.swa.payment_domain.event.ProcessPaymentFailedEvent;
import com.swa.payment_infrastructure.payment_messaging.mapper.PaymentEventMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer implements IEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PaymentEventMapper paymentEventMapper;

    public void publish(String topic, Object payload) {
        try {
            kafkaTemplate.send(topic, payload).whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent message=[{}] with offset=[{}]",
                            payload,
                            result.getRecordMetadata().offset());
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
    public void publishPaymentSuccess(OrderConfirmationEvent event, PaymentEvent paymentEvent) {
        OrderConfirmationEventAvro orderConfirmationEventAvro = paymentEventMapper
                .toOrderConfirmationEventAvro(event);
        String topic = paymentEvent.getPaymentStatus().getTopic();
        
        log.info("Payment processed successfully for order: {}", orderConfirmationEventAvro.getOrderId());
        OrderPrepareEventAvro orderPrepareEventAvro = paymentEventMapper
                .toOrderPrepareEventAvro(orderConfirmationEventAvro, paymentEvent);
        publish(topic, orderPrepareEventAvro);
    }
    
    @Override
    public void publishPaymentFailure(OrderConfirmationEvent event, PaymentEvent paymentEvent) {
        String topic = paymentEvent.getPaymentStatus().getTopic();
        OrderConfirmationEventAvro orderConfirmationEventAvro = paymentEventMapper
                .toOrderConfirmationEventAvro(event);
        
        log.error("Payment failed for order: {}", orderConfirmationEventAvro.getOrderId());
        publish(topic, orderConfirmationEventAvro);
    }

    @Override
    public void publishPaymentFailure(ProcessPaymentFailedEvent event, PaymentEvent paymentEvent) {
        String topic = paymentEvent.getPaymentStatus().getTopic();
        ProcessPaymentFailedEventAvro failedEventAvro = paymentEventMapper
                .toProcessPaymentFailedEventAvro(event, paymentEvent);
        
        log.error("Payment failed for order: {}", failedEventAvro.getOrderId());
        publish(topic, failedEventAvro);
    }


    @Override
    public void publishBalanceCreationFailed(UserBalance userBalance, PaymentEvent event) {
        CreateUserBalanceFailedEventAvro eventAvro = paymentEventMapper
                .toCreateUserBalanceFailedEventAvro(userBalance);
        String topic = event.getPaymentStatus().getTopic();
        publish(topic, eventAvro);
    }
}
