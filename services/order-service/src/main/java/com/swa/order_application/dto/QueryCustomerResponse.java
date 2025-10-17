package com.swa.order_application.dto;

import com.swa.order_domain.entity.Customer;

public class QueryCustomerResponse {
    private String message;
    private String status;
    private Customer customer;

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
}
