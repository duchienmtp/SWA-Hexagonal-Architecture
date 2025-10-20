package com.swa.restaurant_application.ports.output;

import java.util.Optional;

import com.swa.restaurant_domain.entity.Inventory;
import com.swa.restaurant_domain.entity.Product;
import com.swa.restaurant_domain.entity.Restaurant;
import com.swa.restaurant_domain.valueobject.ProductId;
import com.swa.restaurant_domain.valueobject.RestaurantId;

public interface IRestaurantRepository {
    Inventory save(Inventory inventory);
    Restaurant save(Restaurant restaurant);
    Product save(Product product);
    Optional<Restaurant> findRestaurantById(RestaurantId restaurantId);
    Optional<Inventory> findInventoryByRestaurantAndProduct(RestaurantId restaurantId, ProductId productId);
}
