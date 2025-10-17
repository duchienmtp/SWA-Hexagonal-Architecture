package com.swa.customer_application.ports.output.repository;

import java.util.List;
import java.util.Optional;

import com.swa.customer_domain.entity.Customer;

public interface ICustomerRepository {
    // Persist order-agnostic of MySQL, MongoDB, or InMemory
    Customer save(Customer customer);
    List<Customer> findAll();
    Optional<Customer> findById(String customerId);
}
