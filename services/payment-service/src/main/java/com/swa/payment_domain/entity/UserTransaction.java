package com.swa.payment_domain.entity;

import com.swa.payment_domain.valueobject.CustomerId;
import com.swa.payment_domain.valueobject.Money;
import com.swa.payment_domain.valueobject.OrderId;
import com.swa.payment_domain.valueobject.PaymentMethod;
import com.swa.payment_domain.valueobject.PaymentStatus;
import com.swa.payment_domain.valueobject.TransactionId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTransaction {
    private TransactionId id;
    private OrderId orderId;
    private CustomerId customerId;
    private Money amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
}
