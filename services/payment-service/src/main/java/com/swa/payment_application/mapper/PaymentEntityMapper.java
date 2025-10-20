package com.swa.payment_application.mapper;

import org.springframework.stereotype.Component;


import com.swa.payment_domain.entity.UserBalance;
import com.swa.payment_domain.entity.UserTransaction;
import com.swa.payment_domain.event.OrderConfirmationEvent;
import com.swa.payment_domain.valueobject.CustomerId;
import com.swa.payment_domain.valueobject.Money;
import com.swa.payment_domain.valueobject.PaymentMethod;
import com.swa.payment_domain.valueobject.PaymentStatus;
import com.swa.payment_domain.valueobject.TransactionId;

@Component
public class PaymentEntityMapper {
    public UserTransaction toUserTransactionsEntity(OrderConfirmationEvent orderConfirmationEvent,
            UserBalance userBalance) {
        return UserTransaction.builder()
                .id(TransactionId.of(java.util.UUID.randomUUID()))
                .orderId(orderConfirmationEvent.getOrderId())
                .customerId(CustomerId.toCustomerId(orderConfirmationEvent.getCustomer().getId()))
                .amount(new Money(orderConfirmationEvent.getTotalAmount().getAmount()))
                .paymentMethod(PaymentMethod.CREDIT_CARD) // Example, adjust as needed
                .paymentStatus(PaymentStatus.PAYMENT_SUCCESS)
                .build();
    }
}
