package com.swa.order_infrastructure.order_dataaccess.adapter;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.swa.order_application.ports.output.repository.IOrderRepository;
import com.swa.order_domain.entity.Order;
import com.swa.order_domain.event.OrderApprovalEvent;
import com.swa.order_domain.event.ProcessPaymentFailedEvent;
import com.swa.order_domain.exception.OrderDomainException;
import com.swa.order_domain.valueobject.OrderId;
import com.swa.order_domain.valueobject.OrderStatus;
import com.swa.order_infrastructure.order_dataaccess.entity.OrderJpaEntity;
import com.swa.order_infrastructure.order_dataaccess.mapper.OrderDataAccessMapper;
import com.swa.order_infrastructure.order_dataaccess.repository.IOrderJpaRepository;

@Component
@RequiredArgsConstructor
public class OrderRepository implements IOrderRepository {
    
    private final IOrderJpaRepository orderJpaRepository;  // Uses JPA repository
    private final OrderDataAccessMapper mapper;           // Converts entities
    
    @Override
    public Order save(Order order) {
        // 1. Convert domain → JPA
        OrderJpaEntity jpaEntity = mapper.toJpaEntity(order);
        
        // 2. Use Spring Data JPA repository
        OrderJpaEntity saved = orderJpaRepository.save(jpaEntity);
        
        // 3. Convert JPA → domain
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Order> findById(OrderId orderId) {
        return orderJpaRepository.findById(orderId.getValue())
            .map(mapper::toDomain);
    }

    @Override
    public Order cancelOrder(OrderId orderId) {
        OrderJpaEntity order = orderJpaRepository.findById(orderId.getValue())
        .orElseThrow(() -> new OrderDomainException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderDomainException("Order cannot be cancelled because it is in CANCELLED state");
        }
    
        order.setStatus(OrderStatus.CANCELLED);
        OrderJpaEntity updatedOrder = orderJpaRepository.save(order);
        return mapper.toDomain(updatedOrder);
    }

    @Override
    public void cancelOrder(ProcessPaymentFailedEvent event) {
        OrderJpaEntity order = orderJpaRepository.findById(event.getOrderId().getValue())
        .orElseThrow(() -> new OrderDomainException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new OrderDomainException("Order cannot be cancelled because it is in CANCELLED state");
        }
    
        order.setStatus(OrderStatus.CANCELLED);
        order.setFailureMessages(event.getMessage());
        orderJpaRepository.save(order);
    }

    @Override
    public void approveOrder(OrderApprovalEvent event) {
        OrderJpaEntity order = orderJpaRepository.findById(event.getOrderId().getValue())
        .orElseThrow(() -> new OrderDomainException("Order not found"));

        if (order.getStatus() == OrderStatus.APPROVED) {
            throw new OrderDomainException("Order cannot be approved because it is in APPROVED state");
        }
    
        order.setStatus(OrderStatus.APPROVED);
        order.setFailureMessages(null);
        orderJpaRepository.save(order);
    }
}
