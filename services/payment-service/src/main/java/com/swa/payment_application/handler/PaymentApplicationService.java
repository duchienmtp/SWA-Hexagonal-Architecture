package com.swa.payment_application.handler;

import com.swa.payment_application.mapper.PaymentEntityMapper;
import com.swa.payment_application.ports.input.IPaymentApplicationService;
import com.swa.payment_application.ports.output.IEventPublisher;
import com.swa.payment_application.ports.output.IPaymentRepository;
import com.swa.payment_domain.entity.UserBalance;
import com.swa.payment_domain.entity.UserTransaction;
import com.swa.payment_domain.event.OrderConfirmationEvent;
import com.swa.payment_domain.event.PaymentEvent;
import com.swa.payment_domain.event.ProcessPaymentFailedEvent;
import com.swa.payment_domain.valueobject.CustomerId;
import com.swa.payment_domain.valueobject.Money;
import com.swa.payment_domain.valueobject.OrderId;
import com.swa.payment_domain.valueobject.PaymentStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentApplicationService implements IPaymentApplicationService {
    private final IPaymentRepository _paymentRepository;
    private final IEventPublisher _eventPublisher;
    private final PaymentEntityMapper paymentEntityMapper;
    
    @Override
    @Transactional
    public void handleOrderConfirmation(OrderConfirmationEvent event) {
        PaymentEvent paymentEvent = processPayment(event);

        if (paymentEvent.getPaymentStatus() == PaymentStatus.PAYMENT_SUCCESS) {
            _eventPublisher.publishPaymentSuccess(event, paymentEvent);
        } else {
            _eventPublisher.publishPaymentFailure(event, paymentEvent);
        }
    }

    public PaymentEvent processPayment(OrderConfirmationEvent event) {
        try {
            CustomerId customerId = CustomerId.toCustomerId(event.getCustomer().getId());
            UserBalance userBalance = _paymentRepository.findUserBalanceById(customerId).orElse(null);
            if (userBalance == null) {
                return new PaymentEvent(PaymentStatus.PAYMENT_FAILED, "User balance with ID: " + customerId + " not found");
            }
    
            Money orderAmount = event.getTotalAmount();
            if (!userBalance.hasSufficientFunds(orderAmount)) {
                log.error("Insufficient funds for customer ID: {}", customerId);
                return new PaymentEvent(PaymentStatus.PAYMENT_FAILED, "Insufficient funds");
            }
    
            UserTransaction payment = paymentEntityMapper.toUserTransactionsEntity(event, userBalance);
            userBalance = userBalance.deduct(orderAmount);
            _paymentRepository.save(payment);
            _paymentRepository.save(userBalance);

            return new PaymentEvent(PaymentStatus.PAYMENT_SUCCESS, "Payment processed successfully");
        } catch (Exception e) {
            log.error("Error processing payment for order: {}", event.getOrderId(), e);
            return new PaymentEvent(PaymentStatus.PAYMENT_FAILED, e.getMessage());
        }
    }

    @Override
    public void createUserBalance(UserBalance userBalance) {
        try {
            _paymentRepository.save(userBalance);
            log.info("User balance created successfully for customer ID: {}", userBalance.getCustomerId());
        } catch (Exception e) {
            log.error("Error creating user balance for customer ID: {}", userBalance.getCustomerId(), e);
            PaymentEvent paymentEvent = new PaymentEvent(PaymentStatus.CREATE_USER_BALANCE_FAILED, e.getMessage());
            _eventPublisher.publishBalanceCreationFailed(userBalance, paymentEvent);
        }
    }

    @Override
    public void handleRefundPayment(ProcessPaymentFailedEvent event) {
        PaymentEvent paymentEvent = processRefundPayment(event);
        _eventPublisher.publishPaymentFailure(event, paymentEvent);
    }

    
    @Transactional
    public PaymentEvent processRefundPayment(ProcessPaymentFailedEvent event) {
        OrderId orderId = event.getOrderId();
        try {
            UserTransaction transaction = _paymentRepository.findTransactionByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found for order ID: " + orderId));

            UserBalance userBalance = _paymentRepository.findUserBalanceById(transaction.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("User balance not found for customer ID: " + transaction.getCustomerId()));

            userBalance.getBalance().add(transaction.getAmount());
            transaction.setPaymentStatus(PaymentStatus.PAYMENT_REFUNDED);

            _paymentRepository.save(transaction);
            _paymentRepository.save(userBalance);

            log.info("Refund processed successfully for order ID: {}", orderId);
            return new PaymentEvent(PaymentStatus.PAYMENT_REFUNDED, "Refund processed successfully");
        } catch (Exception e) {
            log.error("Error processing refund for order ID: {}", orderId, e);
            throw new RuntimeException(e);
        }
    }
}
