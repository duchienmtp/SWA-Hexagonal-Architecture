package com.swa.notification_domain.valueobject;

import java.util.UUID;
import lombok.Value;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Value
public class ProductId {
    @JsonProperty("value")
    private final UUID value;

    @JsonCreator
    public ProductId(@JsonProperty("value") UUID value) {
        this.value = value;
    }

    public static ProductId of(UUID value) {
        return new ProductId(value);
    }

    public static ProductId toProductId(String value) {
        return new ProductId(UUID.fromString(value));
    }
}
