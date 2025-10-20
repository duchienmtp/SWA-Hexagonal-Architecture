package com.swa.notification_application.ports.input;

import com.swa.notification_domain.event.OrderConfirmationEvent;

public interface INotificationApplicationService {
    void sendOrderConfirmationNotification(OrderConfirmationEvent event);
}
