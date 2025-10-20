package com.swa.payment_application.ports.output;

import com.swa.payment_domain.entity.UserBalance;
import com.swa.payment_domain.event.OrderConfirmationEvent;
import com.swa.payment_domain.event.PaymentEvent;
import com.swa.payment_domain.event.ProcessPaymentFailedEvent;

public interface IEventPublisher {
    void publishPaymentSuccess(OrderConfirmationEvent event, PaymentEvent paymentEvent);
    void publishPaymentFailure(ProcessPaymentFailedEvent event, PaymentEvent paymentEvent);
    void publishPaymentFailure(OrderConfirmationEvent event, PaymentEvent paymentEvent);
    void publishBalanceCreationFailed(UserBalance userBalance, PaymentEvent event);
}
