package com.restaurant.erp.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation đánh dấu các API yêu cầu tính lũy đẳng (Idempotency),
 * như tạo đơn hàng hoặc xử lý thanh toán, dựa trên header Idempotency-Key.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    /**
     * Thời gian lưu trữ cache idempotency (tính theo giờ). Mặc định 24h.
     */
    long expireHours() default 24;
}
