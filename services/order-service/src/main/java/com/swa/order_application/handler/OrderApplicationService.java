package com.swa.order_application.handler;

import com.swa.order_application.dto.*;
import com.swa.order_application.mapper.OrderDataMapper;
import com.swa.order_application.ports.input.service.IOrderApplicationService;
import com.swa.order_application.ports.output.event.IEventPublisher;
import com.swa.order_application.ports.output.event.IRabbitMQEventPublisher;
import com.swa.order_application.ports.output.repository.IOrderRepository;
import com.swa.order_application.ports.output.service.ICustomerFeignService;
import com.swa.order_domain.entity.*;
import com.swa.order_domain.event.OrderApprovalEvent;
import com.swa.order_domain.event.ProcessPaymentFailedEvent;
import com.swa.order_domain.exception.OrderDomainException;
import com.swa.order_domain.service.OrderDomainService;
import com.swa.order_domain.valueobject.OrderId;
import com.swa.order_infrastructure.order_messaging.producer.OrderRabbitMQPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

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
    private final IRabbitMQEventPublisher _rabbitMQEventPublisher;
    private final IEventPublisher _eventPublisher;

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand command) {
        try {
            var customerResponse = customerClient.findCustomerById(command.getCustomerId().toString())
                    .orElseThrow(() -> new OrderDomainException(
                            "Cannot create order:: No customer exists with the provided ID"));

            var customer = customerResponse.getCustomer();

            log.info("Customer found: {}", customer);
            Order order = orderDataMapper.toOrder(command);

            order = orderDomainService.validateAndInitializeOrder(order);

            Order savedOrder = _orderRepository.save(order);

            // Kafka
            // _eventPublisher.sendOrderConfirmationEvent(savedOrder, customer);

            // _eventPublisher.sendOrderPurchaseEvent(savedOrder, customer);

            // RabbitMQ
            _rabbitMQEventPublisher.sendOrderConfirmationMessage(savedOrder, customer);

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
                    .orElseThrow(() -> new OrderDomainException(
                            "Cannot track order:: No customer exists with the provided ID"));

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
                    .orElseThrow(() -> new OrderDomainException(
                            "Cannot cancel order:: No customer exists with the provided ID"));

            var response = _orderRepository.cancelOrder(new OrderId(command.getOrderId()));

            var trackingOrder = orderDataMapper.toTrackingOrderDTO(response);

            return orderDataMapper.toCancelOrderResponseDTO(trackingOrder, "Order cancelled successfully", 200);
        } catch (Exception e) {
            log.error("Failed to cancel order: {}", e.getMessage());
            throw new OrderDomainException("Failed to cancel order", e);
        }
    }

    @Override
    public void cancelOrder(ProcessPaymentFailedEvent command) {
        try {
            var customerResponse = customerClient.findCustomerById(command.getCustomerId().getValue().toString())
                    .orElseThrow(() -> new OrderDomainException(
                            "Cannot cancel order:: No customer exists with the provided ID"));

            _orderRepository.cancelOrder(command);

        } catch (Exception e) {
            log.error("Failed to cancel order: {}", e.getMessage());
            throw new OrderDomainException("Failed to cancel order", e);
        }
    }

    @Override
    @Transactional
    public void approveOrder(OrderApprovalEvent command) {
        Optional<Order> order = Optional.empty();

        try {
            order = _orderRepository.findById(command.getOrderId());

            if (order.isEmpty()) {
                log.error("Cannot approve order: Order with ID {} not found", command.getOrderId().getValue());
                _eventPublisher.sendRestaurantInventoryRollbackEvent(order.get(), command.getCustomerId(),
                        "Order with ID " + command.getOrderId().getValue() + " not found");
                return;
            }

            String customerId = command.getCustomerId().getValue().toString();
            var customerResponse = customerClient.findCustomerById(customerId);

            if (customerResponse.isEmpty()) {
                log.error("Cannot approve order: No customer exists with ID {}", customerId);
                _eventPublisher.sendRestaurantInventoryRollbackEvent(order.get(), command.getCustomerId(),
                        "No customer exists with ID " + customerId);
                return;
            }

            _orderRepository.approveOrder(command);
            log.info("Order with ID {} successfully approved",
                    command.getOrderId().getValue());

        } catch (Exception e) {
            log.error("Failed to approve order: {}", e.getMessage(), e);

            if (order.isPresent()) {
                _eventPublisher.sendRestaurantInventoryRollbackEvent(order.get(), command.getCustomerId(),
                        "Exception during order approval: " + e.getMessage());
            }
        }
    }
}
