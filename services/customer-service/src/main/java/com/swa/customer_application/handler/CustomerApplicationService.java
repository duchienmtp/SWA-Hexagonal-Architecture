package com.swa.customer_application.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.swa.customer_application.dto.*;
import com.swa.customer_application.mapper.CustomerDataMapper;
import com.swa.customer_application.ports.input.service.ICustomerService;
import com.swa.customer_domain.entity.Customer;
import com.swa.customer_domain.exception.CustomerDomainException;
import com.swa.customer_application.ports.output.repository.ICustomerRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerApplicationService implements ICustomerService {
    private final ICustomerRepository _customerRepository;
    private final CustomerDataMapper customerDataMapper;
  
    @Override
    public CreateCustomerResponse createCustomer(CreateCustomerCommand comnand) {
        try {
            Customer customer = customerDataMapper.toCustomer(comnand);

            Customer savedCustomer = _customerRepository.save(customer);

            return CreateCustomerResponse.builder()
                    .customerId(savedCustomer.getId().getValue())
                    .message("Customer created successfully")
                    .status("201")
                    .build();
        } catch (Exception e) {
            log.error("Error in CustomerService.createCustomer: ", e);
            throw new CustomerDomainException("Error in CustomerService.createCustomer: ", e);
        }
    }

    @Override
    public QueryAllCustomersResponse findAll() {
        try {
            List<Customer> customers = _customerRepository.findAll();
            return QueryAllCustomersResponse.builder()
                    .customers(customers)
                    .message("Customers found successfully")
                    .status("200")
                    .build();
        } catch (Exception e) {
            log.error("Error in CustomerService.findAll: ", e);
            throw new CustomerDomainException("Error in CustomerService.findAll: ", e);
        }
    }

    @Override
    public QueryCustomerResponse findById(String id) {
        try {
            Customer customer = _customerRepository.findById(id)
                .orElseThrow(() -> new CustomerDomainException("Customer with ID " + id + " not found"));
            return QueryCustomerResponse.builder()
                    .customer(customer)
                    .message("Customer found successfully")
                    .status("200")
                    .build();
        } catch (Exception e) {
            log.error("Error in CustomerService.findById: ", e);
            throw new CustomerDomainException("Error in CustomerService.findById: ", e);
        }
    }
}
