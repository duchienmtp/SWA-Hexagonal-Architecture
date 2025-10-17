package com.swa.notification_domain.valueobject;

import java.util.UUID;
import lombok.Value;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Value
public class OrderId {
    @JsonProperty("value")
    private final UUID value;

    @JsonCreator
    public OrderId(@JsonProperty("value") UUID value) {
        this.value = value;
    }

    public static OrderId of(UUID value) {
        return new OrderId(value);
    }
}
