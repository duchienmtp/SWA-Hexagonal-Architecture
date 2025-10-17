package com.swa.order_application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackingOrderDTO {
    private UUID trackingId;
    private UUID orderId;
    private UUID customerId;
    private UUID restaurantId;
    private String orderStatus;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> items;

}
