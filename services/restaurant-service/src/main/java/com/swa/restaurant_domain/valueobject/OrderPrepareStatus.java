package com.swa.restaurant_domain.valueobject;

public enum OrderPrepareStatus {
    ORDER_PREPARE_SUCCESS("order-prepare-success-topic"),
    ORDER_PREPARE_FAILED("order-prepare-failed-topic");

    private final String topic;

    OrderPrepareStatus(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
