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
        var formattedDomainEntity = customers.stream().map(customerDataAccessMapper::toCustomer).toList();
        return formattedDomainEntity;
    }

    @Override
    public Optional<Customer> findById(String id) {
        var customerEntity = customerJpaRepository.findById(id);
        var formattedDomainEntity = customerDataAccessMapper.toCustomer(customerEntity.get());
        return Optional.of(formattedDomainEntity);  
    }

    @Override
    public void deleteById(String customerId) {
        customerJpaRepository.deleteById(customerId);
    }
}
