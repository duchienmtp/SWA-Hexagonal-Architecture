package com.swa.order_application.dto;

import java.math.BigDecimal;
import java.util.List;
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
public class CreateOrderCommand {
    @NotNull(message = "Customer ID is required")
    private UUID customerId;
    
    @NotNull(message = "Restaurant ID is required")
    private UUID restaurantId;

    @NotNull(message = "Total price is required")
    private BigDecimal price;

    @NotNull(message = "Order Items are required")
    private List<CreateOrderItemDTO> items;

    @NotNull(message = "Address is required")
    private CreateStreetAddressDTO address;
}
