package com.restaurant.erp.table.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.erp.table.entity.DiningTable.TableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu tạo mới bàn ăn")
public class CreateTableRequest {

    @Schema(description = "ID khu vực đặt bàn", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Vui lòng chọn khu vực trực thuộc")
    private Integer areaId;

    @Schema(description = "Tên hoặc số hiệu bàn ăn", example = "Bàn 01", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Tên bàn không được để trống")
    @Size(min = 1, max = 50, message = "Tên bàn phải từ 1 đến 50 ký tự")
    private String name;

    @Schema(description = "Mã số bàn (tùy chọn)")
    private String tableNumber;

    @Schema(description = "Sức chứa tối đa của bàn (số ghế)", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Sức chứa bàn không được để trống")
    @Min(value = 1, message = "Sức chứa tối thiểu từ 1 khách trở lên")
    @Max(value = 100, message = "Sức chứa tối đa không vượt quá 100 khách")
    private Integer capacity;

    @Schema(description = "Trạng thái khởi tạo của bàn", example = "AVAILABLE")
    @Builder.Default
    private TableStatus status = TableStatus.AVAILABLE;

    public String getName() {
        return name != null && !name.isBlank() ? name.trim() : tableNumber;
    }
}
