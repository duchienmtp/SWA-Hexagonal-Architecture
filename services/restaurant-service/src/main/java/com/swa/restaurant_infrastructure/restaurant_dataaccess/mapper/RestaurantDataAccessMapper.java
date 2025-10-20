package com.swa.restaurant_infrastructure.restaurant_dataaccess.mapper;

import org.springframework.stereotype.Component;

import com.swa.restaurant_domain.entity.Inventory;
import com.swa.restaurant_domain.entity.Product;
import com.swa.restaurant_domain.entity.Restaurant;
import com.swa.restaurant_domain.valueobject.InventoryId;
import com.swa.restaurant_domain.valueobject.Money;
import com.swa.restaurant_domain.valueobject.ProductId;
import com.swa.restaurant_domain.valueobject.RestaurantId;
import com.swa.restaurant_infrastructure.restaurant_dataaccess.entity.InventoryJpaEntity;
import com.swa.restaurant_infrastructure.restaurant_dataaccess.entity.ProductJpaEntity;
import com.swa.restaurant_infrastructure.restaurant_dataaccess.entity.RestaurantJpaEntity;

@Component
public class RestaurantDataAccessMapper {
    public ProductJpaEntity toJpaEntity(Product product) {
        return ProductJpaEntity.builder()
                .id(product.getId().getValue())
                .name(product.getName())
                .build();
    }

    public Product toDomain(ProductJpaEntity jpaEntity) {
        return jpaEntity == null ? null : Product.builder()
                .id(new ProductId(jpaEntity.getId()))
                .name(jpaEntity.getName())
                .build();
    }

    public RestaurantJpaEntity toJpaEntity(Restaurant restaurant) {
        return RestaurantJpaEntity.builder()
                .id(restaurant.getId().getValue())
                .name(restaurant.getName())
                .build();
    }

    public Restaurant toDomain(RestaurantJpaEntity jpaEntity) {
        return jpaEntity == null ? null : Restaurant.builder()
                .id(new RestaurantId(jpaEntity.getId()))
                .name(jpaEntity.getName())
                .build();
    }

    public InventoryJpaEntity toJpaEntity(Inventory inventory) {
        // Get the entities we'll need
        RestaurantJpaEntity restaurantEntity = toJpaEntity(inventory.getRestaurant());
        ProductJpaEntity productEntity = toJpaEntity(inventory.getProduct());

        // Use the custom builder methods which handle the composite key
        return InventoryJpaEntity.builder()
                .restaurant(restaurantEntity) // This sets both the restaurant and restaurantId in the composite key
                .product(productEntity) // This sets both the product and productId in the composite key
                .quantity(inventory.getQuantity())
                .price(inventory.getPrice().getAmount())
                .build();
    }

    public Inventory toDomain(InventoryJpaEntity jpaEntity) {
        return jpaEntity == null ? null : Inventory.builder()
                .id(InventoryId.of(jpaEntity.getId().getProductId(), jpaEntity.getId().getRestaurantId()))
                .restaurant(toDomain(jpaEntity.getRestaurant()))
                .product(toDomain(jpaEntity.getProduct()))
                .quantity(jpaEntity.getQuantity())
                .price(new Money(jpaEntity.getPrice()))
                .build();
    }
}
