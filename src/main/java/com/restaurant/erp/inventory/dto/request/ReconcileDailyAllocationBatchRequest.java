package com.restaurant.erp.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu chốt kiểm kê bếp cuối ngày theo lô")
public class ReconcileDailyAllocationBatchRequest {

    @Schema(description = "ID chi nhánh chốt kiểm kê", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Chi nhánh không được để trống")
    private Integer branchId;

    @Schema(description = "Ngày chốt kiểm kê", example = "2026-10-02", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Ngày chốt kiểm kê không được để trống")
    private LocalDate allocationDate;

    @Schema(description = "Tên người kiểm kê đối soát")
    private String reconciledBy;

    @Schema(description = "Danh sách chi tiết kiểm kê các nguyên liệu", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Danh sách kiểm kê không được rỗng")
    @Valid
    private List<ReconcileDailyAllocationItemRequest> items;

    @Schema(description = "Ghi chú chung buổi chốt")
    private String generalNote;
}
