package com.swa.payment_infrastructure.payment_dataaccess.mapper;

import org.springframework.stereotype.Component;

import com.swa.payment_domain.entity.UserTransaction;
import com.swa.payment_domain.valueobject.CustomerId;
import com.swa.payment_domain.valueobject.Money;
import com.swa.payment_domain.valueobject.OrderId;
import com.swa.payment_domain.valueobject.TransactionId;
import com.swa.payment_infrastructure.payment_dataaccess.entity.UserTransactionsJpaEntity;

@Component
public class UserTransactionsDataAccessMapper {

    // Domain → JPA (for saving)
    public UserTransactionsJpaEntity toJpaEntity(UserTransaction userTransaction) {
        return UserTransactionsJpaEntity.builder()
            .id(userTransaction.getId() != null ? userTransaction.getId().getValue() : null)
            .orderId(userTransaction.getOrderId() != null ? userTransaction.getOrderId().getValue() : null)
            .customerId(userTransaction.getCustomerId() != null ? userTransaction.getCustomerId().getValue() : null)
            .amount(userTransaction.getAmount() != null ? userTransaction.getAmount().getAmount() : null)
            .paymentMethod(userTransaction.getPaymentMethod() != null ? userTransaction.getPaymentMethod() : null)
            .paymentStatus(userTransaction.getPaymentStatus() != null ? userTransaction.getPaymentStatus() : null)
            .build();
    }
    
    // JPA → Domain (after loading)
    public UserTransaction toDomain(UserTransactionsJpaEntity entity) {
        return UserTransaction.builder()
            .id(TransactionId.of(entity.getId()))
            .orderId(OrderId.of(entity.getOrderId()))
            .customerId(CustomerId.of(entity.getCustomerId()))
            .amount(new Money(entity.getAmount()))
            .paymentMethod(entity.getPaymentMethod())
            .paymentStatus(entity.getPaymentStatus())
            .build();
    }
}