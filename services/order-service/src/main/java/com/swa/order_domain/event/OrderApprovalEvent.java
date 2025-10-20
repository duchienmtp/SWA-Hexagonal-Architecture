package com.swa.order_domain.event;

import com.swa.order_domain.valueobject.CustomerId;
import com.swa.order_domain.valueobject.OrderId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderApprovalEvent {
    private OrderId orderId;
    private CustomerId customerId;
    private String message;
}
