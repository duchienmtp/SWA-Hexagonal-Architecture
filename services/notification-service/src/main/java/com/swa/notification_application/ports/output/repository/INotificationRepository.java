package com.swa.notification_application.ports.output.repository;

import com.swa.notification_domain.entity.Notification;

public interface INotificationRepository {
    // Persist notification-agnostic of MySQL, MongoDB, or InMemory
    Notification save(Notification notification);

}
