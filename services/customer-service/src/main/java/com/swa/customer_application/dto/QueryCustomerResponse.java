package com.swa.customer_application.dto;

import com.swa.customer_domain.entity.Customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryCustomerResponse {
    private String message;
    private String status;
    private Customer customer;
}
