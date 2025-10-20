package com.swa.customer_application.ports.input.service;


import com.swa.customer_application.dto.*;

public interface ICustomerService {
    // UseCase: Create order
    CreateCustomerResponse createCustomer(CreateCustomerCommand command);

    // UseCase: Find All
    QueryAllCustomersResponse findAll();

    // UseCase: Find By Id
    QueryCustomerResponse findById(String id);

    void deleteCustomer(String id);
}
