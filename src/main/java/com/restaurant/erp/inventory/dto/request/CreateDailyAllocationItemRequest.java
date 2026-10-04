package com.restaurant.erp.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu chi tiết cấp phát 1 nguyên liệu cho bếp trong ngày")
public class CreateDailyAllocationItemRequest {

    @Schema(description = "ID nguyên vật liệu kho chi nhánh", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Mã nguyên liệu không được để trống")
    private Integer ingredientId;

    @Schema(description = "Số lượng cấp phát cho bếp", example = "15.5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Số lượng cấp phát không được để trống")
    @Positive(message = "Số lượng cấp phát bếp phải lớn hơn 0")
    private Double allocatedQuantity;

    @Schema(description = "Nguồn gốc nguyên liệu (KHO_CHI_NHANH, BEP_TRUNG_TAM, NCC)", example = "KHO_CHI_NHANH")
    private String source;

    @Schema(description = "Mã lô nguyên liệu bếp trung tâm (nếu có)")
    private String centralBatchNo;

    @Schema(description = "Ghi chú cấp phát")
    private String note;
}
