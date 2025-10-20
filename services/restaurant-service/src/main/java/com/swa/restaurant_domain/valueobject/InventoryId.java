package com.swa.restaurant_domain.valueobject;

import java.util.UUID;

import lombok.Value;

@Value
public class InventoryId {
    private final RestaurantId restaurantId;
    private final ProductId productId;

    public static InventoryId of(RestaurantId restaurantId, ProductId productId) {
        return new InventoryId(restaurantId, productId);
    }

    public static InventoryId of(UUID restaurantId, UUID productId) {
        return new InventoryId(RestaurantId.of(restaurantId), ProductId.of(productId));
    }

    public static InventoryId toInventoryId(String restaurantId, String productId) {
        return new InventoryId(RestaurantId.of(UUID.fromString(restaurantId)),
                ProductId.of(UUID.fromString(productId)));
    }
}
