package com.swa.payment_domain.valueobject;

public enum PaymentStatus {
    PAYMENT_SUCCESS("order-purchase-success-topic"),
    PAYMENT_FAILED("order-purchase-failed-topic"),
    PAYMENT_REFUNDED("order-purchase-failed-topic"),
    CREATE_USER_BALANCE_SUCCESS(""),
    CREATE_USER_BALANCE_FAILED("create-user-balance-failed-topic");

    private final String topic;

    PaymentStatus(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
