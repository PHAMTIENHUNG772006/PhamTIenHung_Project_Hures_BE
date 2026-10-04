package com.restaurant.erp.pos.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu tạo đơn hàng mới tại bàn")
public class CreateOrderRequest {

    @Schema(description = "ID chi nhánh (tùy chọn)")
    private Integer branchId;

    @Schema(description = "ID bàn ăn", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Bàn ăn không được để trống")
    private Long tableId;

    @Schema(description = "ID nhân viên phục vụ")
    private Long waiterId;

    @Schema(description = "ID nhân viên thu ngân")
    private Long cashierId;

    @Schema(description = "ID lượt đặt bàn (nếu có)")
    private Long bookingId;

    @Schema(description = "Số tiền giảm giá", example = "0")
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Schema(description = "Số tiền thuế VAT", example = "0")
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Schema(description = "Danh sách món gọi khởi tạo ban đầu")
    @Valid
    private List<CreateOrderItemRequest> items;
}
