package com.swa.order_domain.valueobject;

import java.util.UUID;
import lombok.Value;

@Value
public class OrderId {
    private final UUID value;
    
    public static OrderId of(UUID value) {
        return new OrderId(value);
    }
}
