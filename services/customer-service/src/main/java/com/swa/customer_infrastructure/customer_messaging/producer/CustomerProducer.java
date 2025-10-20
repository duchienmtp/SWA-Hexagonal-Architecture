package com.swa.customer_infrastructure.customer_messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.swa.customer_application.ports.output.event.IEventPublisher;
import com.swa.customer_domain.entity.Customer;
import com.swa.customer_infrastructure.customer_messaging.mapper.CustomerEventMapper;
import com.swa.kafka.avro.model.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerProducer implements IEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CustomerEventMapper customerEventMapper;

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
    public void publishCreateUserBalanceEvent(Customer customer) {
        CreateUserBalanceEventAvro createUserBalanceEventAvro = customerEventMapper
                .mapToCreateUserBalanceEventAvro(customer);
        String topic = "create-user-balance-topic";
        publish(topic, createUserBalanceEventAvro);
    }
}
