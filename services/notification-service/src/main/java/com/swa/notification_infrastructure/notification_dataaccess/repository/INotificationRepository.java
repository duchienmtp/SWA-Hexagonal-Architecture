package com.swa.notification_infrastructure.notification_dataaccess.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.swa.notification_infrastructure.notification_dataaccess.entity.NotificationEntity;

public interface INotificationRepository extends MongoRepository<NotificationEntity, String> {

}
