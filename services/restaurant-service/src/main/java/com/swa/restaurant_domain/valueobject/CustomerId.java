package com.swa.restaurant_domain.valueobject;

import java.util.UUID;
import lombok.Value;

@Value
public class CustomerId {
    private final UUID value;

    public static CustomerId of(UUID value) {
        return new CustomerId(value);
    }

    public static CustomerId toCustomerId(String value) {
        return new CustomerId(UUID.fromString(value));
    }
}
