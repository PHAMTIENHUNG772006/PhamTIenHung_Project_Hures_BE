package com.restaurant.erp.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Chi tiết kiểm kê chốt ca nguyên liệu của bếp")
public class ReconcileDailyAllocationItemRequest {

    @Schema(description = "ID bản ghi cấp phát nguyên liệu", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Mã cấp phát không được để trống")
    private Long allocationId;

    @Schema(description = "Lượng thực tế còn lại cuối ngày tại bếp", example = "2.5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Lượng thực tế còn lại không được để trống")
    @PositiveOrZero(message = "Lượng thực tế còn lại không được âm")
    private Double actualRemaining;

    @Schema(description = "Lý do hao hụt bất thường (nếu có)")
    private String lossReason;

    @Schema(description = "Xác nhận trả lại phần dư thừa về kho chi nhánh", example = "true")
    private Boolean returnToStock;
}
