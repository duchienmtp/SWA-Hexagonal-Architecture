package com.swa.payment_domain.entity;


import com.swa.payment_domain.valueobject.CustomerId;
import com.swa.payment_domain.valueobject.Money;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBalance {
    private CustomerId customerId;
    private Money balance;

    public boolean hasSufficientFunds(Money amount) {
        return this.balance.isGreaterThanOrEqual(amount);
    }

    public UserBalance deduct(Money amount) {
        this.balance = this.balance.subtract(amount);
        return this;
    }
}
