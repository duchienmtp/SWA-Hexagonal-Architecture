package com.swa.payment_application.ports.output;

import java.util.Optional;

import com.swa.payment_domain.entity.UserBalance;
import com.swa.payment_domain.entity.UserTransaction;
import com.swa.payment_domain.valueobject.CustomerId;
import com.swa.payment_domain.valueobject.OrderId;
public interface IPaymentRepository {
    UserTransaction save(UserTransaction userTransaction);
    UserBalance save(UserBalance userBalance);
    Optional<UserBalance> findUserBalanceById(CustomerId customerId);
    Optional<UserTransaction> findTransactionByOrderId(OrderId orderId);
}
