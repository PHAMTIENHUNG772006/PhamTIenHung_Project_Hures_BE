package com.restaurant.erp.pos.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.erp.pos.entity.Payment.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Kết quả xử lý thanh toán đơn hàng")
public class PaymentResponse {

    @Schema(description = "ID giao dịch thanh toán", example = "50")
    private Long id;

    @Schema(description = "ID đơn hàng", example = "101")
    private Long orderId;

    @Schema(description = "Số tiền đã thanh toán (VNĐ)", example = "495000")
    private BigDecimal amount;

    @Schema(description = "Phương thức thanh toán", example = "CASH")
    private PaymentMethod method;

    @Schema(description = "Mã giao dịch đối soát")
    private String transactionReference;

    @Schema(description = "Thời gian ghi nhận thanh toán")
    private ZonedDateTime paidAt;
}
