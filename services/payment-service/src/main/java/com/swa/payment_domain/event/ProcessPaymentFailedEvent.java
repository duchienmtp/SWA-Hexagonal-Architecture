package com.swa.payment_domain.event;

import com.swa.payment_domain.valueobject.CustomerId;
import com.swa.payment_domain.valueobject.OrderId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProcessPaymentFailedEvent {
    private OrderId orderId;
    private CustomerId customerId;
    private String message;
}
