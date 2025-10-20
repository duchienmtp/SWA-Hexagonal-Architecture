package com.swa.restaurant_infrastructure.restaurant_dataaccess.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.swa.restaurant_domain.valueobject.InventoryId;
import com.swa.restaurant_infrastructure.restaurant_dataaccess.entity.InventoryJpaEntity;

@Repository
public interface IInventoryJpaRepository extends JpaRepository<InventoryJpaEntity, InventoryId> {
    @Query("SELECT i FROM InventoryJpaEntity i JOIN FETCH i.restaurant JOIN FETCH i.product WHERE i.restaurant.id = :restaurantId AND i.product.id = :productId")
    Optional<InventoryJpaEntity> findByRestaurantIdAndProductId(@Param("restaurantId") UUID restaurantId, @Param("productId") UUID productId);
}
