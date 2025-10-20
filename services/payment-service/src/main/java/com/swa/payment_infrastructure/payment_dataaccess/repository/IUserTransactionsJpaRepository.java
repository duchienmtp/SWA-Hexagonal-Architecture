package com.swa.payment_infrastructure.payment_dataaccess.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


import com.swa.payment_infrastructure.payment_dataaccess.entity.UserTransactionsJpaEntity;

public interface IUserTransactionsJpaRepository extends JpaRepository<UserTransactionsJpaEntity, UUID> {
    Optional<UserTransactionsJpaEntity> findByOrderId(UUID orderId);
}
