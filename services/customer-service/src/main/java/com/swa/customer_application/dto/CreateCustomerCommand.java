package com.swa.customer_application.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomerCommand {
    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Customer Fullname is required")
    private String fullName;

    @NotNull(message = "Customer email is required")
    private String email;

    @NotNull(message = "Address is required")
    private CreateStreetAddressDTO address;

    @NotNull(message = "Balance is required")
    private Double balance;
}
