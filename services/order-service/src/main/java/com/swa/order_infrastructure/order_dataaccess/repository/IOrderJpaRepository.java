package com.swa.order_infrastructure.order_dataaccess.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swa.order_infrastructure.order_dataaccess.entity.OrderJpaEntity;

public interface IOrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {
    Optional<OrderJpaEntity> findByTrackingId(UUID trackingId);
    // OrderJpaEntity cancelOrder(UUID orderId);
}
