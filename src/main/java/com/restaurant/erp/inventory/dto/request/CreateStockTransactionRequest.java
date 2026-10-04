package com.restaurant.erp.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Schema(description = "Yêu cầu nhập kho nguyên vật liệu")
public class CreateStockTransactionRequest {

    @Schema(description = "ID nguyên vật liệu chi nhánh", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Nguyên vật liệu không được để trống")
    private Integer ingredientId;

    @Schema(description = "Loại giao dịch (IMPORT, EXPORT, ADJUSTMENT, RETURN)", example = "IMPORT")
    @Builder.Default
    private String transactionType = "IMPORT";

    @Schema(description = "Số lượng nhập kho", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive(message = "Số lượng phải lớn hơn 0")
    private double quantity;

    @Schema(description = "Đơn giá nhập kho (VNĐ)", example = "150000")
    @PositiveOrZero(message = "Đơn giá không được âm")
    @Builder.Default
    private double unitPrice = 0.0;

    @Schema(description = "Ghi chú giao dịch")
    private String note;
}
