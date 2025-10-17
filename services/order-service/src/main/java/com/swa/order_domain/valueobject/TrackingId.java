package com.swa.order_domain.valueobject;

import java.util.UUID;
import lombok.Value;

@Value
public class TrackingId {
    private final UUID value;
    
    public static TrackingId newTrackingId() {
        return new TrackingId(UUID.randomUUID());
    }

    public static TrackingId of(UUID value) {
        return new TrackingId(value);
    }
}
