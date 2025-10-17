package com.swa.order_application.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.swa.order_application.dto.CancelOrderResponse;
import com.swa.order_application.dto.CreateOrderCommand;
import com.swa.order_application.dto.CreateOrderResponse;
import com.swa.order_application.dto.TrackOrderResponse;
import com.swa.order_application.dto.TrackingOrderDTO;
import com.swa.order_domain.entity.*;
import com.swa.order_domain.valueobject.*;

@Component
@RequiredArgsConstructor
public class OrderDataMapper {
    private final OrderItemDataMapper orderItemDataMapper;  
    
    public Order toOrder(CreateOrderCommand command) {
        return Order.builder()
            .customerId(new CustomerId(command.getCustomerId()))
            .restaurantId(new RestaurantId(command.getRestaurantId()))
            .deliveryAddress(new StreetAddress(command.getAddress().getStreet(), command.getAddress().getPostalCode(), command.getAddress().getCity()))
            .price(new Money(command.getPrice()))
            .items(command.getItems().stream()
                .map(item -> orderItemDataMapper.toOrderItem(item))
                .toList())
            .build();
    }

    public CreateOrderResponse toCreateOrderResponseDTO(Order order, String message, int status) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .message(message)
                .status(String.valueOf(status))
                .build();
    }

    public TrackingOrderDTO toTrackingOrderDTO(Order order) {
        return TrackingOrderDTO.builder()
                .trackingId(order.getTrackingId().getValue())
                .orderId(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .restaurantId(order.getRestaurantId().getValue())
                .orderStatus(order.getOrderStatus().name())
                .totalAmount(order.getPrice().getAmount())
                .items(order.getItems().stream()
                        .map(item -> orderItemDataMapper.toOrderItemDTO(item))
                        .toList())
                .build();
    }

    public TrackOrderResponse toTrackOrderResponseDTO(TrackingOrderDTO order, String message, int status) {
        return TrackOrderResponse.builder()
                .trackingOrder(order)
                .message(message)
                .status(String.valueOf(status))
                .build();
    }

    public CancelOrderResponse toCancelOrderResponseDTO(TrackingOrderDTO order, String message, int status) {
        return CancelOrderResponse.builder()
                .trackingOrder(order)
                .message(message)
                .status(String.valueOf(status))
                .build();
    }
}
