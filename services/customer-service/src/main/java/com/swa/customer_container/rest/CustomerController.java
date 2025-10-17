package com.swa.customer_container.rest;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swa.customer_application.dto.CreateCustomerCommand;
import com.swa.customer_application.dto.CreateCustomerResponse;
import com.swa.customer_application.dto.QueryAllCustomersResponse;
import com.swa.customer_application.dto.QueryCustomerResponse;
import com.swa.customer_application.ports.input.service.ICustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/customers")
@Slf4j
public class CustomerController {
    private final ICustomerService _customerService;

    @GetMapping(value = "test")
    public ResponseEntity<Object> test() {
        return ResponseEntity.ok(Map.of("message", "Test", "status", 200));
    }

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<CreateCustomerResponse> createCustomer(
            @RequestBody @Valid CreateCustomerCommand request) {
        CreateCustomerResponse response = _customerService.createCustomer(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<QueryAllCustomersResponse> findAll() {
        QueryAllCustomersResponse response = _customerService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customer-id}")
    public ResponseEntity<QueryCustomerResponse> findById(
    @PathVariable("customer-id") String customerId) {
        QueryCustomerResponse response = _customerService.findById(customerId);
        return ResponseEntity.ok(response);
    }
}
