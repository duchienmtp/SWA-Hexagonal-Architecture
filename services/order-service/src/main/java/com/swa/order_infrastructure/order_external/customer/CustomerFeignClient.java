package com.swa.order_infrastructure.order_external.customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.swa.order_application.dto.QueryCustomerResponse;

import java.util.Optional;

@FeignClient(
    name = "customer-service",
    url = "${application.config.customer-url}"
)
public interface CustomerFeignClient {
    @GetMapping("/{customer-id}")
    Optional<QueryCustomerResponse> findCustomerById(@PathVariable("customer-id") String customerId);
}
