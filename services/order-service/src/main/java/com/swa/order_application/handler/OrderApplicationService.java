package com.swa.order_application.handler;

import com.swa.order_application.dto.*;
import com.swa.order_application.mapper.OrderDataMapper;
import com.swa.order_application.ports.input.service.IOrderApplicationService;
import com.swa.order_application.ports.output.event.IEventPublisher;
import com.swa.order_application.ports.output.repository.IOrderRepository;
import com.swa.order_application.ports.output.service.ICustomerFeignService;
import com.swa.order_domain.entity.*;
import com.swa.order_domain.exception.OrderDomainException;
import com.swa.order_domain.service.OrderDomainService;
import com.swa.order_domain.valueobject.OrderId;
import com.swa.order_infrastructure.order_messaging.producer.OrderRabbitMQPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApplicationService implements IOrderApplicationService {
    private final IOrderRepository _orderRepository; // Domain port!
    private final OrderDataMapper orderDataMapper;
    private final OrderDomainService orderDomainService;
    private final ICustomerFeignService customerClient;
    private final OrderRabbitMQPublisher orderRabbitMQPublisher;
    private final IEventPublisher _eventPublisher;

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand command) {
        try {
            var customerResponse = customerClient.findCustomerById(command.getCustomerId().toString())
                .orElseThrow(() -> new OrderDomainException("Cannot create order:: No customer exists with the provided ID"));

            var customer = customerResponse.getCustomer();

            log.info("Customer found: {}", customer);
            Order order = orderDataMapper.toOrder(command);

            order = orderDomainService.validateAndInitializeOrder(order);

            Order savedOrder = _orderRepository.save(order);

            // Kafka
            _eventPublisher.sendOrderConfirmationEvent(savedOrder, customer);

            _eventPublisher.sendOrderPurchaseEvent(savedOrder, customer);

            // RabbitMQ
            // orderRabbitMQPublisher.sendOrderConfirmationMessage(
            //     orderEventMapper.toOrderConfirmationEvent(savedOrder, customer)
            // );

            return orderDataMapper.toCreateOrderResponseDTO(savedOrder, "Order created successfully", 201);
        } catch (Exception e) {
            log.error("Failed to create order: {}", e.getMessage());
            throw new OrderDomainException("Failed to create order", e);
        }
    }

    @Override
    public TrackOrderResponse trackOrder(TrackOrderQuery query) {
        try {
            var customerResponse = customerClient.findCustomerById(query.getCustomerId().toString())
            .orElseThrow(() -> new OrderDomainException("Cannot track order:: No customer exists with the provided ID"));

            var response = _orderRepository.findById(new OrderId(query.getOrderId()));

            var trackingOrder = orderDataMapper.toTrackingOrderDTO(response.get());
            
            return orderDataMapper.toTrackOrderResponseDTO(trackingOrder, "Order tracked successfully", 200);
        } catch (Exception e) {
            log.error("Failed to retrieve order: {}", e.getMessage());
            throw new OrderDomainException("Failed to retrieve order", e);
        }
    }

    @Override
    public CancelOrderResponse cancelOrder(CancelOrderCommand command) {
        try {
            var customerResponse = customerClient.findCustomerById(command.getCustomerId().toString())
            .orElseThrow(() -> new OrderDomainException("Cannot cancel order:: No customer exists with the provided ID"));

            var response = _orderRepository.cancelOrder(new OrderId(command.getOrderId()));

            var trackingOrder = orderDataMapper.toTrackingOrderDTO(response);
            
            return orderDataMapper.toCancelOrderResponseDTO(trackingOrder, "Order cancelled successfully", 200);
        } catch (Exception e) {
            log.error("Failed to cancel order: {}", e.getMessage());
            throw new OrderDomainException("Failed to cancel order", e);
        }
    }
}
