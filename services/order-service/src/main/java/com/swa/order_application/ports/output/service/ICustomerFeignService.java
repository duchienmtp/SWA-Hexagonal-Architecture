package com.swa.order_application.ports.output.service;

import java.util.Optional;

import com.swa.order_application.dto.QueryCustomerResponse;

public interface ICustomerFeignService {
    Optional<QueryCustomerResponse> findCustomerById(String id);
}
