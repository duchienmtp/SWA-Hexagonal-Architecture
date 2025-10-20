package com.swa.restaurant_domain.entity;

import java.util.HashSet;
import java.util.Set;

import com.swa.restaurant_domain.valueobject.ProductId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Product {
    private ProductId id;
    private String name;
    private Set<Inventory> inventories;

        public boolean isAvailableAt(Restaurant restaurant) {
        if (inventories == null) return false;
        return inventories.stream()
            .anyMatch(inventory -> 
                inventory.getRestaurant().equals(restaurant) && 
                inventory.getQuantity() > 0);
    }
    
    public Set<Restaurant> getAvailableRestaurants() {
        if (inventories == null) return new HashSet<>();
        
        Set<Restaurant> restaurants = new HashSet<>();
        inventories.stream()
            .filter(inventory -> inventory.getQuantity() > 0)
            .forEach(inventory -> restaurants.add(inventory.getRestaurant()));
        
        return restaurants;
    }
}
