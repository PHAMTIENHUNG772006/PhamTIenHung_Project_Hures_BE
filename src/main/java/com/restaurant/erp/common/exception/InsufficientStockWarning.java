package com.restaurant.erp.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InsufficientStockWarning {
    private final Integer ingredientId;
    private final String ingredientName;
    private final double availableStock;
    private final double requiredStock;
    private final String message;
}
