package com.swa.restaurant_infrastructure.restaurant_dataaccess.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "restaurant_id")
    private UUID restaurantId;

    @Column(name = "product_id")
    private UUID productId;
}