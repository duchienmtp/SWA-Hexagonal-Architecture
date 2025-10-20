package com.swa.restaurant_infrastructure.restaurant_dataaccess.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurants")
public class RestaurantJpaEntity {
    @Id
    private UUID id;
    
    private String name;
    
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<InventoryJpaEntity> inventories = new HashSet<>();
}