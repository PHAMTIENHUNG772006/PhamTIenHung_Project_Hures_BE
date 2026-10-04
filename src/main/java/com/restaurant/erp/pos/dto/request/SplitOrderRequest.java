package com.restaurant.erp.pos.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu tách món ăn sang bàn mới")
public class SplitOrderRequest {

    @Schema(description = "Danh sách ID các món cần tách sang bàn mới", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Danh sách món cần tách không được để rỗng")
    private List<Long> itemIds;

    @Schema(description = "ID bàn ăn đích cần chuyển món sang", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Mã bàn mới không được để trống")
    private Long newTableId;
}
