package com.swa.payment_domain.valueobject;

import java.util.UUID;
import lombok.Value;

@Value
public class TransactionId {
    private final UUID value;

    public static TransactionId of(UUID value) {
        return new TransactionId(value);
    }

    public static TransactionId toTransactionId(String value) {
        return new TransactionId(UUID.fromString(value));
    }
}
