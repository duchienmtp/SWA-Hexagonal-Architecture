package com.swa.customer_application.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.swa.customer_application.dto.CreateCustomerCommand;
import com.swa.customer_application.dto.CreateCustomerResponse;
import com.swa.customer_domain.entity.*;
import com.swa.customer_domain.valueobject.*;

@Component
@RequiredArgsConstructor
public class CustomerEntityMapper {
    public Customer toCustomer(CreateCustomerCommand command) {
        return Customer.builder()
            .id(new CustomerId(command.getCustomerId()))
            .fullName(command.getFullName())
            .email(command.getEmail())
            .address(new Address(command.getAddress().getStreet(), command.getAddress().getPostalCode(), command.getAddress().getCity()))
            .balance(command.getBalance())
            .build();
    }

    public CreateCustomerResponse toCreateCustomerResponse(Customer customer) {
        return CreateCustomerResponse.builder()
            .customerId(customer.getId().getValue())
            .message("Customer created successfully")
            .status("201")
            .build();
    }
}
