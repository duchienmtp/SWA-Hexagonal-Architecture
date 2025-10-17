package com.swa.customer_application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateStreetAddressDTO {
    @NotBlank private String street;
    @NotBlank private String postalCode;
    @NotBlank private String city;
}
