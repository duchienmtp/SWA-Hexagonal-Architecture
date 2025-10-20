package com.swa.customer_infrastructure.customer_dataaccess.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.swa.customer_infrastructure.customer_dataaccess.entity.CustomerEntity;

public interface ICustomerJpaRepository extends MongoRepository<CustomerEntity, String> {
    Optional<CustomerEntity> findById(String id);
}
