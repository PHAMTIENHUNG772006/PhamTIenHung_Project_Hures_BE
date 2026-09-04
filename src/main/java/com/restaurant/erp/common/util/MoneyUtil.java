package com.restaurant.erp.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtil {

    public static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static BigDecimal round(BigDecimal value) {
        if (value == null) return BigDecimal.ZERO;
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
