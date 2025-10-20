package com.swa.restaurant_infrastructure.restaurant_dataaccess.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swa.restaurant_infrastructure.restaurant_dataaccess.entity.ProductJpaEntity;

public interface IRestaurantJpaRepository extends JpaRepository<ProductJpaEntity, UUID> {

}
