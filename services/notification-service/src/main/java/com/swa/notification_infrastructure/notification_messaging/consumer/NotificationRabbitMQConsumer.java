package com.swa.notification_infrastructure.notification_messaging.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.swa.notification_application.ports.output.service.IEmailService;
import com.swa.notification_domain.entity.OrderItem;
import com.swa.notification_domain.event.OrderConfirmationEvent;
import com.swa.notification_domain.exception.NotificationDomainException;
import com.swa.notification_infrastructure.notification_dataaccess.repository.INotificationRepository;
import com.swa.notification_infrastructure.notification_messaging.mapper.NotificationEventMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationRabbitMQConsumer {
    private final INotificationRepository _notificationRepository;
    private final IEmailService _emailService;
    private final NotificationEventMapper notificationEventMapper;
    
    @RabbitListener(queues = "${spring.rabbitmq.queues.order-created-queue}")
    public void listen(OrderConfirmationEvent orderConfirmationEvent) {
        try {
            // Process the consumed message
            log.info("Consumed message from order-created-queue: " + orderConfirmationEvent);

            throw new RuntimeException("Oh no! A simulated failure to test retry!");
            // var notification = notificationEventMapper.toNotificationEntity(orderConfirmationEvent);
            // _notificationRepository.save(notification);

            // var customerName = orderConfirmationEvent.getCustomer().fullName();
            // _emailService.sendOrderConfirmationEmail(
            //         orderConfirmationEvent.getCustomer().email(),
            //         customerName,
            //         orderConfirmationEvent.getTotalAmount().getAmount(),
            //         orderConfirmationEvent.getOrderId(),
            //         orderConfirmationEvent.getItems().stream().map(
            //                 item -> OrderItem.builder()
            //                         .productId(item.getProductId())
            //                         .price(item.getPrice())
            //                         .quantity(item.getQuantity())
            //                         .build())
            //                 .toList());
        } catch (Exception e) {
            log.error("Error processing message from order-created-queue: " + orderConfirmationEvent);
            throw new NotificationDomainException("Failed to process message: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.order-created-queue.dlq}")
    public void processFailedMessage(Message failedMessage, OrderConfirmationEvent event) {
        log.error("========= RECEIVED FAILED MESSAGE IN DLQ =========");
        log.error("Failed Event Payload: {}", event);
        
        // Header 'x-death' chứa thông tin về lý do message bị "chết"
        // Ví dụ: số lần retry, tên exchange ban đầu, lý do...
        if (failedMessage.getMessageProperties().getHeaders().get("x-death") != null) {
            log.error("Reason for failure: {}", failedMessage.getMessageProperties().getHeaders().get("x-death"));
        }

        // Tại đây bạn có thể:
        // 1. Gửi email cảnh báo cho admin
        // 2. Lưu thông tin lỗi vào một bảng trong database
        // 3. Bỏ qua nếu không cần xử lý
    }
}