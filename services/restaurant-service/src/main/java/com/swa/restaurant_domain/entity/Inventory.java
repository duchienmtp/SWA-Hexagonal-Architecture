package com.swa.restaurant_domain.entity;

import com.swa.restaurant_domain.exception.RestaurantDomainException;
import com.swa.restaurant_domain.valueobject.InventoryId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Inventory {
    private InventoryId id;
    private Restaurant restaurant;
    private Product product;
    private Money price;
    private Integer quantity;

    public void increaseQuantity(int amount) {
        if (amount < 0) {
            throw new RestaurantDomainException("Amount to increase must be positive");
        }
        this.quantity += amount;
    }
    
    public void decreaseQuantity(int amount) {
        if (amount < 0) {
            throw new RestaurantDomainException("Amount to decrease must be positive");
        }
        
        if (this.quantity < amount) {
            throw new RestaurantDomainException("Not enough inventory available");
        }
        
        this.quantity -= amount;
    }
    
    public boolean isAvailable(int requestedQuantity) {
        return this.quantity >= requestedQuantity;
    }
}
