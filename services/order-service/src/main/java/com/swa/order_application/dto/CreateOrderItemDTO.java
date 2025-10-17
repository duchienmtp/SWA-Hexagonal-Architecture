package com.swa.order_application.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderItemDTO {
    @NotNull private UUID productId;
    @Min(1) private int quantity;
    @NotNull private BigDecimal price;
    // Optional: client-provided subtotal; usually computed server-side
    private BigDecimal subTotal;
}
