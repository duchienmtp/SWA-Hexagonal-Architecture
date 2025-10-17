package com.swa.order_domain.valueobject;

import java.util.UUID;
import lombok.Value;

@Value
public class CustomerId {
    private final UUID value;
    
    public static CustomerId of(UUID value) {
        return new CustomerId(value);
    }
}
