package com.swa.payment_infrastructure.payment_dataaccess.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swa.payment_infrastructure.payment_dataaccess.entity.UserBalanceJpaEntity;

public interface IUserBalanceJpaRepository extends JpaRepository<UserBalanceJpaEntity, UUID> {
}
