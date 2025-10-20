package com.swa.restaurant_infrastructure.restaurant_dataaccess.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.swa.restaurant_application.ports.output.IRestaurantRepository;
import com.swa.restaurant_domain.entity.Inventory;
import com.swa.restaurant_domain.entity.Product;
import com.swa.restaurant_domain.entity.Restaurant;
import com.swa.restaurant_domain.valueobject.ProductId;
import com.swa.restaurant_domain.valueobject.RestaurantId;
import com.swa.restaurant_infrastructure.restaurant_dataaccess.entity.InventoryJpaEntity;
import com.swa.restaurant_infrastructure.restaurant_dataaccess.entity.ProductJpaEntity;
import com.swa.restaurant_infrastructure.restaurant_dataaccess.entity.RestaurantJpaEntity;
import com.swa.restaurant_infrastructure.restaurant_dataaccess.mapper.RestaurantDataAccessMapper;
import com.swa.restaurant_infrastructure.restaurant_dataaccess.repository.IInventoryJpaRepository;
import com.swa.restaurant_infrastructure.restaurant_dataaccess.repository.IProductJpaRepository;
import com.swa.restaurant_infrastructure.restaurant_dataaccess.repository.IRestaurantJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RestaurantRepository implements IRestaurantRepository {
    private final IRestaurantJpaRepository restaurantJpaRepository;
    private final IProductJpaRepository productJpaRepository;
    private final IInventoryJpaRepository inventoryJpaRepository;
    private final RestaurantDataAccessMapper mapper;

    @Override
    public Product save(Product product) {
        // 1. Convert domain → JPA
        ProductJpaEntity jpaEntity = mapper.toJpaEntity(product);

        // 2. Use Spring Data JPA repository
        ProductJpaEntity saved = productJpaRepository.save(jpaEntity);

        // 3. Convert JPA → domain
        return mapper.toDomain(saved);
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        // 1. Convert domain → JPA
        RestaurantJpaEntity jpaEntity = mapper.toJpaEntity(restaurant);

        // 2. Use Spring Data JPA repository
        RestaurantJpaEntity saved = restaurantJpaRepository.save(jpaEntity);

        // 3. Convert JPA → domain
        return mapper.toDomain(saved);
    }

    @Override
    public Inventory save(Inventory inventory) {
        // 1. Convert domain → JPA
        InventoryJpaEntity jpaEntity = mapper.toJpaEntity(inventory);

        // 2. Use Spring Data JPA repository
        InventoryJpaEntity saved = inventoryJpaRepository.save(jpaEntity);

        // 3. Convert JPA → domain
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Restaurant> findRestaurantById(RestaurantId restaurantId) {
        Optional<RestaurantJpaEntity> jpaEntityOptional = restaurantJpaRepository.findById(restaurantId.getValue());
        return jpaEntityOptional.map(jpaEntity -> mapper.toDomain(jpaEntity));
    }

    @Override
    public Optional<Inventory> findInventoryByRestaurantAndProduct(RestaurantId restaurantId, ProductId productId) {
        Optional<InventoryJpaEntity> inventoryJpaEntityOptional = inventoryJpaRepository.findByRestaurantIdAndProductId(
                restaurantId.getValue(),
                productId.getValue());

        return inventoryJpaEntityOptional.map(mapper::toDomain);
    }

}
