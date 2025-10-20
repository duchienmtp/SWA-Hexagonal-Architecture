package com.swa.restaurant_domain.entity;

import java.util.HashSet;
import java.util.Set;

import com.swa.restaurant_domain.valueobject.RestaurantId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Restaurant {
    private RestaurantId id;
    private String name;
    private Set<Inventory> inventories;

    public void addInventory(Inventory inventory) {
        if (inventories == null) {
            inventories = new HashSet<>();
        }
        inventories.add(inventory);
    }
    
    public void removeInventory(Inventory inventory) {
        if (inventories != null) {
            inventories.remove(inventory);
        }
    }
    
    public boolean hasProduct(Product product) {
        if (inventories == null) return false;
        return inventories.stream()
            .anyMatch(inventory -> inventory.getProduct().equals(product));
    }
    
    public Integer getProductQuantity(Product product) {
        if (inventories == null) return 0;
        return inventories.stream()
            .filter(inventory -> inventory.getProduct().equals(product))
            .map(Inventory::getQuantity)
            .findFirst()
            .orElse(0);
    }
}
