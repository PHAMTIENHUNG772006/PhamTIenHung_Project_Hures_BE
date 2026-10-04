package com.restaurant.erp.table.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.erp.table.entity.DiningTable.TableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Thông tin chi tiết bàn ăn")
public class TableResponse {

    @Schema(description = "ID bàn ăn", example = "1")
    private Integer id;

    @Schema(description = "ID chi nhánh trực thuộc", example = "1")
    private Integer branchId;

    @Schema(description = "ID khu vực đặt bàn", example = "1")
    private Integer areaId;

    @Schema(description = "Tên khu vực đặt bàn", example = "Tầng 1 - Sân Vườn")
    private String areaName;

    @Schema(description = "Mã số bàn", example = "Bàn 01")
    private String tableNumber;

    @Schema(description = "Tên bàn ăn", example = "Bàn 01")
    private String name;

    @Schema(description = "Sức chứa tối đa (chỗ ngồi)", example = "4")
    private Integer capacity;

    @Schema(description = "Trạng thái bàn ăn", example = "available")
    private TableStatus status;

    @Schema(description = "Thời gian tạo")
    private ZonedDateTime createdAt;

    public String getName() {
        return name != null && !name.isEmpty() ? name : tableNumber;
    }

    public String getTableNumber() {
        return tableNumber != null && !tableNumber.isEmpty() ? tableNumber : name;
    }
}
