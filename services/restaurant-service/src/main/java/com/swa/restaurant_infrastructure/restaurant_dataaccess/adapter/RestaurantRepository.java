package com.swa.restaurant_infrastructure.restaurant_dataaccess.adapter;

import org.springframework.stereotype.Component;

import com.swa.restaurant_application.ports.output.IRestaurantRepository;
import com.swa.restaurant_infrastructure.restaurant_dataaccess.entity.ProductJpaEntity;
import com.swa.restaurant_infrastructure.restaurant_dataaccess.repository.IRestaurantJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RestaurantRepository implements IRestaurantRepository {
    private final IRestaurantJpaRepository restaurantJpaRepository;

    // @Override
    // public Product save(Product order) {
    //     // 1. Convert domain → JPA
    //     ProductJpaEntity jpaEntity = mapper.toJpaEntity(order);
        
    //     // 2. Use Spring Data JPA repository
    //     ProductJpaEntity saved = restaurantJpaRepository.save(jpaEntity);
        
    //     // 3. Convert JPA → domain
    //     return mapper.toDomain(saved);
    // }
}
