package com.swa.order_domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.swa.order_domain.exception.OrderDomainException;

@DisplayName("Money value object unit tests")
class MoneyTest {

    @Test
    @DisplayName("add combines amounts")
    void add_ShouldReturnCombinedAmount() {
        Money base = new Money(BigDecimal.valueOf(40));
        Money increment = new Money(BigDecimal.valueOf(60));

        Money result = base.add(increment);

        assertEquals(0, result.getAmount().compareTo(BigDecimal.valueOf(100)));
    }

    @Test
    @DisplayName("multiply scales by integer")
    void multiply_ShouldScaleAmount() {
        Money base = new Money(BigDecimal.valueOf(25));

        Money result = base.multiply(4);

        assertEquals(0, result.getAmount().compareTo(BigDecimal.valueOf(100)));
    }

    @Test
    @DisplayName("constructor rejects negative values")
    void constructor_ShouldRejectNegativeAmount() {
        assertThrows(OrderDomainException.class, () -> new Money(BigDecimal.valueOf(-5)));
    }
}
