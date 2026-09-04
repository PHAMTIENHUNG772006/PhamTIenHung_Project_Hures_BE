package com.restaurant.erp.pos.dto;

import com.restaurant.erp.pos.entity.Payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod method;
    private String transactionReference;
    private ZonedDateTime paidAt;
}
