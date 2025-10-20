package com.swa.customer_application.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swa.customer_application.dto.*;
import com.swa.customer_application.mapper.CustomerEntityMapper;
import com.swa.customer_application.ports.input.service.ICustomerService;
import com.swa.customer_domain.entity.Customer;
import com.swa.customer_domain.exception.CustomerDomainException;
import com.swa.customer_application.ports.output.event.IEventPublisher;
import com.swa.customer_application.ports.output.repository.ICustomerRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerApplicationService implements ICustomerService {
    private final ICustomerRepository _customerRepository;
    private final IEventPublisher _eventPublisher;
    private final CustomerEntityMapper customerEntityMapper;
  
    @Override
    @Transactional
    public CreateCustomerResponse createCustomer(CreateCustomerCommand command) {
        try {
            Customer customer = customerEntityMapper.toCustomer(command);

            Customer savedCustomer = _customerRepository.save(customer);

            _eventPublisher.publishCreateUserBalanceEvent(customer);

            return customerEntityMapper.toCreateCustomerResponse(savedCustomer);
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

    @Override
    @Transactional
    public void deleteCustomer(String id) {
        try {
            _customerRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error in CustomerService.deleteCustomer: ", e);
            throw new CustomerDomainException("Error in CustomerService.deleteCustomer: ", e);
        }
    }
}
