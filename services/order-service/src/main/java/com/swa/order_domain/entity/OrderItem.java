package com.swa.order_domain.entity;

import com.swa.order_domain.exception.OrderDomainException;
import com.swa.order_domain.valueobject.Money;
import com.swa.order_domain.valueobject.OrderId;
import com.swa.order_domain.valueobject.ProductId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private OrderId orderId;
    private ProductId productId;
    private Money price;
    private int quantity;
    private Money subTotal;

    // Business logic
    public void validateItem() {
        if (quantity <= 0) {
            throw new OrderDomainException("Quantity must be positive");
        }
        if (!price.isGreaterThanZero()) {
            throw new OrderDomainException("Price must be positive");
        }
        if (!subTotal.isGreaterThanZero()) {
            throw new OrderDomainException("SubTotal must be positive");
        }
    }

    public boolean isPriceValid() {
        return price.isGreaterThanZero();
    }

    public Money calculateSubtotal() {
        return price.multiply(quantity);
    }

    public void initializeOrderItem(OrderId orderId, ProductId productId, Money price, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
        this.subTotal = calculateSubtotal();
    }
}
