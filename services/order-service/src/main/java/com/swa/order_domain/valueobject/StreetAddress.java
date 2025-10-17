package com.swa.order_domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StreetAddress {
    private final String street;
    private final String postalCode;
    private final String city;
}
