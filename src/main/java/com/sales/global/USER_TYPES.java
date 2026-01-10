package com.sales.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum USER_TYPES {
    WHOLESALER("W"),
    RETAILER("R"),
    STAFF("S"),
    SUPER_ADMIN("SA");
    private final String type;
}
