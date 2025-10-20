package com.swa.restaurant_infrastructure.restaurant_dataaccess.entity;

import java.math.BigDecimal;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventories")
public class InventoryJpaEntity {

    @EmbeddedId
    private InventoryId id;

    private BigDecimal price;

    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER) 
    @MapsId("restaurantId") // Maps to the restaurantId field in InventoryId
    @JoinColumn(name = "restaurant_id", nullable = false) 
    private RestaurantJpaEntity restaurant;

    @ManyToOne(fetch = FetchType.EAGER) 
    @MapsId("productId") // Maps to the productId field in InventoryId
    @JoinColumn(name = "product_id", nullable = false)
    private ProductJpaEntity product;

    // Replace the standard @Builder with a custom builder implementation
    public static InventoryJpaEntityBuilder builder() {
        return new InventoryJpaEntityBuilder();
    }

    // Custom builder implementation
    @NoArgsConstructor
    public static class InventoryJpaEntityBuilder {
        private InventoryId id;
        private BigDecimal price;
        private Integer quantity;
        private RestaurantJpaEntity restaurant;
        private ProductJpaEntity product;

        public InventoryJpaEntityBuilder id(InventoryId id) {
            this.id = id;
            return this;
        }

        public InventoryJpaEntityBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public InventoryJpaEntityBuilder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        // Enhanced builder method for restaurant
        public InventoryJpaEntityBuilder restaurant(RestaurantJpaEntity restaurant) {
            this.restaurant = restaurant;
            if (this.id == null) {
                this.id = new InventoryId();
            }
            if (restaurant != null) {
                this.id.setRestaurantId(restaurant.getId());
            }
            return this;
        }

        // Enhanced builder method for product
        public InventoryJpaEntityBuilder product(ProductJpaEntity product) {
            this.product = product;
            if (this.id == null) {
                this.id = new InventoryId();
            }
            if (product != null) {
                this.id.setProductId(product.getId());
            }
            return this;
        }

        public InventoryJpaEntity build() {
            return new InventoryJpaEntity(this.id, this.price, this.quantity, this.restaurant, this.product);
        }

        public String toString() {
            return "InventoryJpaEntity.InventoryJpaEntityBuilder(id=" + this.id +
                    ", price=" + this.price +
                    ", quantity=" + this.quantity + ")";
        }
    }
}
