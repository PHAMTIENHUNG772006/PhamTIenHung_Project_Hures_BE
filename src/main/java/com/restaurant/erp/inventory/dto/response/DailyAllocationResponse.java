package com.restaurant.erp.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Thông tin chi tiết cấp phát và kiểm kê nguyên liệu bếp trong ngày")
public class DailyAllocationResponse {

    @Schema(description = "ID bản ghi cấp phát", example = "1")
    private Long id;

    @Schema(description = "ID chi nhánh", example = "1")
    private Integer branchId;

    @Schema(description = "Tên chi nhánh", example = "Chi nhánh Landmark 81")
    private String branchName;

    @Schema(description = "ID nguyên liệu", example = "5")
    private Integer ingredientId;

    @Schema(description = "Tên nguyên liệu", example = "Thịt bò thăn")
    private String ingredientName;

    @Schema(description = "Mã SKU", example = "ING-THIT-BO")
    private String sku;

    @Schema(description = "Đơn vị tính", example = "kg")
    private String unit;

    @Schema(description = "Đơn giá vốn đơn vị", example = "280000.0")
    private Double costPerUnit;

    @Schema(description = "Ngày cấp phát", example = "2026-10-02")
    private LocalDate allocationDate;

    @Schema(description = "Lượng cấp phát đầu ngày", example = "20.0")
    private Double allocatedQuantity;

    @Schema(description = "Lượng đã dùng theo POS", example = "15.0")
    private Double usedQuantity;

    @Schema(description = "Lượng thực tế còn lại tại bếp sau chốt ca", example = "4.5")
    private Double actualRemaining;

    @Schema(description = "Lượng thu hồi trả lại kho", example = "4.5")
    private Double returnedQuantity;

    @Schema(description = "Lượng hao hụt", example = "0.5")
    private Double lossQuantity;

    @Schema(description = "Lý do hao hụt")
    private String lossReason;

    @Schema(description = "Chi phí hao hụt (VNĐ)", example = "140000.0")
    private Double lossCost;

    @Schema(description = "Nguồn gốc nguyên liệu", example = "KHO_CHI_NHANH")
    private String source;

    @Schema(description = "Mã lô trung tâm")
    private String centralBatchNo;

    @Schema(description = "Trạng thái (ALLOCATED, RECONCILED)", example = "ALLOCATED")
    private String status;

    @Schema(description = "Ghi chú")
    private String note;
}
