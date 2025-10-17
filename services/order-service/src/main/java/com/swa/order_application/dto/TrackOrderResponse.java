package com.swa.order_application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackOrderResponse {
    private TrackingOrderDTO trackingOrder;
    private String message;
    private String status;
}
