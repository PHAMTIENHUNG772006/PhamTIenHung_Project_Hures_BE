package com.restaurant.erp.pos.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Schema(description = "Yêu cầu thêm món vào đơn hàng")
public class CreateOrderItemRequest {

    @Schema(description = "ID món ăn trong thực đơn", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Món ăn không được để trống")
    private Integer menuItemId;

    @Schema(description = "Số lượng gọi món", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Số lượng không được để trống")
    @Positive(message = "Số lượng món gọi phải lớn hơn 0")
    private Integer quantity;

    @Schema(description = "Đơn giá tùy chỉnh (nếu có)")
    private BigDecimal price;

    @Schema(description = "Ghi chú đặc biệt cho món ăn (VD: ít cay, không hành)")
    private String note;
}
