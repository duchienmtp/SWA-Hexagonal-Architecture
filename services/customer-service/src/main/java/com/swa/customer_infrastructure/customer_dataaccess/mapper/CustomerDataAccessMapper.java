package com.swa.customer_infrastructure.customer_dataaccess.mapper;

import com.swa.customer_domain.entity.Customer;
import com.swa.customer_domain.valueobject.CustomerId;
import com.swa.customer_infrastructure.customer_dataaccess.entity.CustomerEntity;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {

    public Customer toCustomer(CustomerEntity entity) {
        // Implement the mapping from CustomerEntity to Customer
        return Customer.builder()
                .id(entity.getId() != null ? CustomerId.of(UUID.fromString(entity.getId())) : null)
                .fullName(entity.getFullName() != null ? entity.getFullName() : null)
                .email(entity.getEmail() != null ? entity.getEmail() : null)
                .address(entity.getAddress() != null ? entity.getAddress() : null)
                .build();
    }

    public CustomerEntity toCustomerEntity(Customer customer) {
        // Implement the mapping from Customer to CustomerEntity
        return CustomerEntity.builder()
                .id(customer.getId().getValue() != null ? customer.getId().getValue().toString() : null)
                .fullName(customer.getFullName() != null ? customer.getFullName() : null)
                .email(customer.getEmail() != null ? customer.getEmail() : null)
                .address(customer.getAddress() != null ? customer.getAddress() : null)
                .build();
    }
}
