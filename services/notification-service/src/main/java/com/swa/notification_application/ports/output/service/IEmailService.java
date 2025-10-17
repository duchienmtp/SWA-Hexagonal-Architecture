package com.swa.notification_application.ports.output.service;

import java.math.BigDecimal;
import java.util.List;

import com.swa.notification_domain.entity.OrderItem;
import com.swa.notification_domain.valueobject.OrderId;

import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendOrderConfirmationEmail(
            String toEmail,
            String customerName,
            BigDecimal totalAmount,
            OrderId orderId,
            List<OrderItem> items) throws MessagingException;
}
