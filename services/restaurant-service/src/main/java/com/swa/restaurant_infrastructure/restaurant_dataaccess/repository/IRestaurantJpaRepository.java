package com.swa.restaurant_infrastructure.restaurant_dataaccess.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swa.restaurant_infrastructure.restaurant_dataaccess.entity.RestaurantJpaEntity;

@Repository
public interface IRestaurantJpaRepository extends JpaRepository<RestaurantJpaEntity, UUID> {

}
