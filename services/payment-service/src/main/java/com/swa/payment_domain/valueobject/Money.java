package com.swa.payment_domain.valueobject;

import java.math.BigDecimal;

import com.swa.payment_domain.exception.PaymentDomainException;

public class Money {
    // Gợi ý: Sử dụng BigDecimal để xử lý tiền tệ chính xác hơn Double/Float
    private final BigDecimal amount;
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money(BigDecimal amount) {
        if (amount == null) {
            this.amount = BigDecimal.ZERO;
        } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new PaymentDomainException("Money amount cannot be null or negative.");
        } else {
            this.amount = amount;
        }
    }

    public BigDecimal getAmount() {
        return amount;
    }

    // 2. Logic nghiệp vụ (Business Logic)
    // Ví dụ: Phương thức cộng tiền, chỉ định nghĩa logic tại đây
    public Money add(Money money) {
        return new Money(this.amount.add(money.amount));
    }

    public Money subtract(Money money) {
        BigDecimal result = this.amount.subtract(money.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new PaymentDomainException("Resulting money amount cannot be negative.");
        }
        return new Money(result);
    }
    
    // Ví dụ: Phương thức nhân tiền
    public Money multiply(int multiplier) {
        return new Money(this.amount.multiply(new BigDecimal(multiplier)));
    }
    
    public boolean isGreaterThanZero() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return this.amount.compareTo(other.amount) >= 0;
    }
}
