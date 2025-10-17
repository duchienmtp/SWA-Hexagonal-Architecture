package com.swa.customer_infrastructure.customer_dataaccess.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.swa.customer_domain.valueobject.Address;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class CustomerEntity {
    @Id
    private String id;
    
    private String fullName;
    
    private String email;
    
    private Address address;
}
