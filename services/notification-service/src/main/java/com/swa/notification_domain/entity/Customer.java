package com.swa.notification_domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class Customer {
    private final String id;
    private final String fullName;
    private final String email;
}
