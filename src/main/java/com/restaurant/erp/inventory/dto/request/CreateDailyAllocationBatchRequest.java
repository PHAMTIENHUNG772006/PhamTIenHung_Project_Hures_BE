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
@Schema(description = "Yêu cầu cấp phát theo lô nguyên liệu cho bếp trong ngày")
public class CreateDailyAllocationBatchRequest {

    @Schema(description = "ID chi nhánh nhận cấp phát", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Chi nhánh không được để trống")
    private Integer branchId;

    @Schema(description = "Ngày cấp phát thực tế", example = "2026-10-02", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Ngày cấp phát không được để trống")
    private LocalDate allocationDate;

    @Schema(description = "Danh sách chi tiết nguyên vật liệu cấp phát", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Danh sách nguyên liệu cấp phát không được rỗng")
    @Valid
    private List<CreateDailyAllocationItemRequest> items;

    @Schema(description = "Tên người lập phiếu cấp phát", example = "Nguyễn Văn Bếp Trưởng")
    private String creatorName;
}
