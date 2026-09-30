package com.restaurant.erp.branch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.restaurant.erp.branch.entity.emuns.BranchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO Response trả về thông tin chi nhánh nhà hàng")
public class BranchResponse {

    @Schema(description = "Mã định danh ID chi nhánh", example = "1")
    private Integer id;

    @Schema(description = "Mã chi nhánh", example = "CN01")
    private String code;

    @Schema(description = "Tên chi nhánh", example = "Chi nhánh Landmark 81")
    private String name;

    @Schema(description = "Địa chỉ hoạt động", example = "720A Điện Biên Phủ, Phường 22, Bình Thạnh, TP.HCM")
    private String address;

    @Schema(description = "Số điện thoại liên hệ", example = "02838129999")
    private String phone;

    @Schema(description = "Email liên hệ", example = "landmark@restaurant.vn")
    private String email;

    @Schema(description = "Mã số thuế", example = "0315891234-001")
    private String taxCode;

    @Schema(description = "Giờ mở cửa", example = "08:00")
    @JsonFormat(pattern = "HH:mm[:ss]")
    private LocalTime openingTime;

    @Schema(description = "Giờ đóng cửa", example = "22:30")
    @JsonFormat(pattern = "HH:mm[:ss]")
    private LocalTime closingTime;

    @Schema(description = "Đường dẫn ảnh đại diện chi nhánh")
    private String image;

    @Schema(description = "Trạng thái chi nhánh", example = "ACTIVE")
    private BranchStatus status;

    @Schema(description = "Chi nhánh đang hoạt động hay không", example = "true")
    private Boolean isActive;

    @Schema(description = "Tên người quản lý chi nhánh")
    private String managerName;

    @Schema(description = "Tổng số bàn ăn", example = "25")
    private Integer totalTables;

    @Schema(description = "Thời gian tạo")
    private ZonedDateTime createdAt;

    @Schema(description = "Thời gian cập nhật gần nhất")
    private ZonedDateTime updatedAt;
}
