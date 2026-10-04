package com.restaurant.erp.pos.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.erp.pos.entity.Payment.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu thanh toán hóa đơn đơn hàng")
public class ProcessPaymentRequest {

    @Schema(description = "ID đơn hàng cần thanh toán", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Mã đơn hàng không được để trống")
    private Long orderId;

    @Schema(description = "Số tiền thanh toán (VNĐ)", example = "350000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Số tiền thanh toán không được để trống")
    @Positive(message = "Số tiền thanh toán phải lớn hơn 0")
    private BigDecimal amount;

    @Schema(description = "Phương thức thanh toán (CASH, CREDIT_CARD, BANK_TRANSFER, MOMO, VN_PAY)", example = "CASH", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Phương thức thanh toán không được để trống")
    private PaymentMethod method;

    @Schema(description = "Mã tham chiếu giao dịch điện tử (nếu có)")
    private String transactionReference;
}
