package com.swa.notification_domain.valueobject;

import java.math.BigDecimal;

import com.swa.notification_domain.exception.NotificationDomainException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Money {
    // Gợi ý: Sử dụng BigDecimal để xử lý tiền tệ chính xác hơn Double/Float
    @JsonProperty("amount")
    private final BigDecimal amount;
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    @JsonCreator
    public Money(@JsonProperty("amount") BigDecimal amount) {
        if (amount == null) {
            this.amount = BigDecimal.ZERO;
        } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NotificationDomainException("Money amount cannot be null or negative.");
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

    // Ví dụ: Phương thức nhân tiền
    public Money multiply(int multiplier) {
        return new Money(this.amount.multiply(new BigDecimal(multiplier)));
    }

    public boolean isGreaterThanZero() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
