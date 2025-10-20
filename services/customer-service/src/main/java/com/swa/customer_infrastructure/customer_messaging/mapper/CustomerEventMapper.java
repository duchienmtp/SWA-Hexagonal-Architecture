package com.swa.customer_infrastructure.customer_messaging.mapper;

import org.springframework.stereotype.Component;

import com.swa.customer_domain.entity.Customer;
import com.swa.kafka.avro.model.CreateUserBalanceEventAvro;

@Component
public class CustomerEventMapper {

    public CreateUserBalanceEventAvro mapToCreateUserBalanceEventAvro(Customer customer) {
        return CreateUserBalanceEventAvro.newBuilder()
                .setCustomerId(customer.getId().getValue().toString())
                .setBalance(customer.getBalance().toString())
                .build();
    }
}
