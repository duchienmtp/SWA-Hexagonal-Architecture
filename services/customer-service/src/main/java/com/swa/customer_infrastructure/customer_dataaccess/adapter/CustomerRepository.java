package com.swa.customer_infrastructure.customer_dataaccess.adapter;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.swa.customer_domain.entity.Customer;
import com.swa.customer_infrastructure.customer_dataaccess.repository.ICustomerJpaRepository;
import com.swa.customer_infrastructure.customer_dataaccess.mapper.CustomerDataAccessMapper;
import com.swa.customer_application.ports.output.repository.ICustomerRepository;

@Component
@RequiredArgsConstructor
public class CustomerRepository implements ICustomerRepository {
    private final ICustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper customerDataAccessMapper;

    @Override
    public Customer save(Customer customer) {
        var customerEntity = customerDataAccessMapper.toCustomerEntity(customer);
        var savedEntity = customerJpaRepository.save(customerEntity);
        return customerDataAccessMapper.toCustomer(savedEntity);
    }

    @Override
    public List<Customer> findAll() {
        var customers = customerJpaRepository.findAll();
        return customers.stream().map(customerDataAccessMapper::toCustomer).toList();
    }

    @Override
    public Optional<Customer> findById(String id) {
        return customerJpaRepository.findById(id)
                .map(customerDataAccessMapper::toCustomer);
    }

    @Override
    public void deleteById(String customerId) {
        customerJpaRepository.deleteById(customerId);
    }
}
