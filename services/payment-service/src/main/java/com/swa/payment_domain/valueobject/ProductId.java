package com.swa.payment_domain.valueobject;

import java.util.UUID;
import lombok.Value;

@Value
public class ProductId {
    private final UUID value;
    
    public static ProductId of(UUID value) {
        return new ProductId(value);
    }

    public static ProductId toProductId(String value) {
        return new ProductId(UUID.fromString(value));
    }
}
