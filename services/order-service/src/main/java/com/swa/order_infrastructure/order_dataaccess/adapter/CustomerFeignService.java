package com.swa.order_infrastructure.order_dataaccess.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.swa.order_application.dto.QueryCustomerResponse;
import com.swa.order_application.ports.output.service.ICustomerFeignService;
import com.swa.order_infrastructure.order_external.customer.CustomerFeignClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerFeignService implements ICustomerFeignService {
    private final CustomerFeignClient customerFeignClient;
    
    @Override
    public Optional<QueryCustomerResponse> findCustomerById(String id) {
        return customerFeignClient.findCustomerById(id);
    }
}
