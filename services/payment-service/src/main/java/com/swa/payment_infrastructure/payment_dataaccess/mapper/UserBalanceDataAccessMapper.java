package com.swa.payment_infrastructure.payment_dataaccess.mapper;

import org.springframework.stereotype.Component;

import com.swa.payment_domain.entity.UserBalance;
import com.swa.payment_domain.valueobject.CustomerId;
import com.swa.payment_domain.valueobject.Money;
import com.swa.payment_infrastructure.payment_dataaccess.entity.UserBalanceJpaEntity;

@Component
public class UserBalanceDataAccessMapper {

    // Domain → JPA (for saving)
    public UserBalanceJpaEntity toJpaEntity(UserBalance userBalance) {
        return UserBalanceJpaEntity.builder()
            .customerId(userBalance.getCustomerId() != null ? userBalance.getCustomerId().getValue() : null)
            .balance(userBalance.getBalance() != null ? userBalance.getBalance().getAmount() : null)
            .build();
    }
    
    // JPA → Domain (after loading)
    public UserBalance toDomain(UserBalanceJpaEntity entity) {
        return UserBalance.builder()
            .customerId(CustomerId.of(entity.getCustomerId()))
            .balance(new Money(entity.getBalance()))
            .build();
    }
}