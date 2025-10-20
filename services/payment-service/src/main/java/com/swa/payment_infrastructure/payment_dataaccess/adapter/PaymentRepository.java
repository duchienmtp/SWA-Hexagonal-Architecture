package com.swa.payment_infrastructure.payment_dataaccess.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.swa.payment_application.ports.output.IPaymentRepository;
import com.swa.payment_domain.entity.UserBalance;
import com.swa.payment_domain.entity.UserTransaction;
import com.swa.payment_domain.valueobject.CustomerId;
import com.swa.payment_domain.valueobject.OrderId;
import com.swa.payment_infrastructure.payment_dataaccess.entity.UserTransactionsJpaEntity;
import com.swa.payment_infrastructure.payment_dataaccess.mapper.UserBalanceDataAccessMapper;
import com.swa.payment_infrastructure.payment_dataaccess.mapper.UserTransactionsDataAccessMapper;
import com.swa.payment_infrastructure.payment_dataaccess.repository.IUserBalanceJpaRepository;
import com.swa.payment_infrastructure.payment_dataaccess.repository.IUserTransactionsJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentRepository implements IPaymentRepository {
    private final IUserTransactionsJpaRepository _userTransactionsJpaRepository; // Uses JPA repository
    private final IUserBalanceJpaRepository _userBalanceJpaRepository; // Uses JPA repository
    private final UserTransactionsDataAccessMapper userTransactionsDataAccessMapper; // Converts entities
    private final UserBalanceDataAccessMapper userBalanceDataAccessMapper; // Converts entities

    @Override
    public UserTransaction save(UserTransaction userTransaction) {
        // 1. Convert domain → JPA
        UserTransactionsJpaEntity jpaEntity = userTransactionsDataAccessMapper.toJpaEntity(userTransaction);

        // 2. Use Spring Data JPA repository
        UserTransactionsJpaEntity saved = _userTransactionsJpaRepository.save(jpaEntity);

        // 3. Convert JPA → domain
        return userTransactionsDataAccessMapper.toDomain(saved);
    }

    @Override
    public UserBalance save(UserBalance userBalance) {
        var jpaEntity = userBalanceDataAccessMapper.toJpaEntity(userBalance);

        var savedEntity = _userBalanceJpaRepository.save(jpaEntity);

        return userBalanceDataAccessMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<UserBalance> findUserBalanceById(CustomerId customerId) {
        // Implementation for finding UserBalance by customerId
        return _userBalanceJpaRepository.findById(customerId.getValue())
                .map(userBalanceDataAccessMapper::toDomain);
    }

    @Override
    public Optional<UserTransaction> findTransactionByOrderId(OrderId orderId) {
        return _userTransactionsJpaRepository.findByOrderId(orderId.getValue())
            .map(userTransactionsDataAccessMapper::toDomain);
    }
}
