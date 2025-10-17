package com.swa.order_domain.service;

import org.springframework.stereotype.Service;

import com.swa.order_domain.entity.Order;

@Service
public class OrderDomainService {
    public Order validateAndInitializeOrder(Order order) {
        // 1. Initialize order (set tracking ID, status, etc.)
        order.initializeOrder();

        // 2. Validate business rules
        order.validateOrder();
        
        // 3. Any other domain logic that involves multiple aggregates
        // For example:
        // - Check inventory
        // - Calculate pricing
        // - Apply promotions
        
        return order;
    }
}
