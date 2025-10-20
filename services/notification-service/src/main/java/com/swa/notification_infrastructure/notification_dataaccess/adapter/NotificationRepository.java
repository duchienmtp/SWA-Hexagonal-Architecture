package com.swa.notification_infrastructure.notification_dataaccess.adapter;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.swa.notification_application.ports.output.repository.INotificationRepository;
import com.swa.notification_infrastructure.notification_dataaccess.repository.INotificationMongoRepository;
import com.swa.notification_domain.entity.Notification;
import com.swa.notification_infrastructure.notification_dataaccess.entity.NotificationEntity;
import com.swa.notification_infrastructure.notification_dataaccess.mapper.NotificationDataAccessMapper;

@Component
@RequiredArgsConstructor
public class NotificationRepository implements INotificationRepository {
    
    private final INotificationMongoRepository _notificationJpaRepository;  // Uses JPA repository
    private final NotificationDataAccessMapper mapper;           // Converts entities
    
    @Override
    public Notification save(Notification notification) {
        // 1. Convert domain → JPA
        NotificationEntity jpaEntity = mapper.toJpaEntity(notification);
        
        // 2. Use Spring Data JPA repository
        NotificationEntity saved = _notificationJpaRepository.save(jpaEntity);

        // 3. Convert JPA → domain
        return mapper.toDomain(saved);
    }
}
