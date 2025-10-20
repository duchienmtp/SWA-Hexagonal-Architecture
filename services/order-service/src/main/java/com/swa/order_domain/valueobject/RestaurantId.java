package com.swa.order_domain.valueobject;

import java.util.UUID;
import lombok.Value;

@Value
public class RestaurantId {
    private final UUID value;
    
    public static RestaurantId of(UUID value) {
        return new RestaurantId(value);
    }

    public static RestaurantId toRestaurantId(String value) {
        return new RestaurantId(UUID.fromString(value));
    }
}
