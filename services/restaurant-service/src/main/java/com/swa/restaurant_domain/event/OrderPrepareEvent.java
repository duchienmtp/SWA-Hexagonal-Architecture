package com.swa.restaurant_domain.event;

import com.swa.restaurant_domain.valueobject.OrderPrepareStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPrepareEvent {
    private OrderPrepareStatus orderPrepareStatus;
    private String message;
}
