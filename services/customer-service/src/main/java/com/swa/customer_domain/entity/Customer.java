package com.swa.customer_domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.swa.customer_domain.valueobject.Address;
import com.swa.customer_domain.valueobject.CustomerId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer extends AggregateRoot<CustomerId> {
    @JsonIgnore
    private CustomerId id;
    
    @JsonProperty("id")
    public String getIdAsString() {
        return id != null ? id.getValue().toString() : "";
    }
    private String fullName;
    private String email;
    private Address address;
    private Double balance;
}
