package com.swa.payment_application.ports.input;

import com.swa.payment_domain.entity.UserBalance;
import com.swa.payment_domain.event.OrderConfirmationEvent;
import com.swa.payment_domain.event.ProcessPaymentFailedEvent;

public interface IPaymentApplicationService {
    void handlePaymentProcess(OrderConfirmationEvent event);
    void createUserBalance(UserBalance userBalance);
    void handleRefundPayment(ProcessPaymentFailedEvent event);
}
