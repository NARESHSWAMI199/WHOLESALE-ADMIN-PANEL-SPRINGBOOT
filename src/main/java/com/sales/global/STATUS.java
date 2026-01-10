package com.sales.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum STATUS {
    ACTIVE("A"),
    DE_ACTIVE("D");
    private final String status;
}
