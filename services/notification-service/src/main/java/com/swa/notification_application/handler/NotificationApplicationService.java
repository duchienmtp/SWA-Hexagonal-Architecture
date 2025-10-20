package com.swa.notification_application.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swa.notification_application.mapper.NotificationEntityMapper;
import com.swa.notification_application.ports.input.INotificationApplicationService;
import com.swa.notification_application.ports.output.repository.INotificationRepository;
import com.swa.notification_application.ports.output.service.IEmailService;
import com.swa.notification_domain.event.OrderConfirmationEvent;

import jakarta.mail.MessagingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationApplicationService implements INotificationApplicationService {
    private final INotificationRepository _notificationRepository;
    private final IEmailService _emailService;
    private final NotificationEntityMapper notificationEntityMapper;
    
    @Override
    @Transactional
    public void sendOrderConfirmationNotification(OrderConfirmationEvent event) {
        var notification = notificationEntityMapper.toNotificationEntity(event);
        notification = _notificationRepository.save(notification);

        var customerName = event.getCustomer().getFullName();
        try {
            _emailService.sendOrderConfirmationEmail(
                    event.getCustomer().getEmail(),
                    customerName,
                    event.getTotalAmount().getAmount(),
                    event.getOrderId(),
                    event.getItems());
        } catch (MessagingException e) {
            log.error("Error sending order confirmation email:: %s", e.getMessage());
            e.printStackTrace();
        }
    }
}
