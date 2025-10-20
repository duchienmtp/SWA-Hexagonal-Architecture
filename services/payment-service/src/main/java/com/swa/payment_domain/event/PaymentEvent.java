package com.swa.payment_domain.event;

import com.swa.payment_domain.valueobject.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private PaymentStatus paymentStatus;
    private String message;
}
