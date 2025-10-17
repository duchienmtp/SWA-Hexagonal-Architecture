package com.swa.customer_application.dto;

import java.util.List;

import com.swa.customer_domain.entity.Customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryAllCustomersResponse {
    private String message;
    private String status;
    private List<Customer> customers;
}
