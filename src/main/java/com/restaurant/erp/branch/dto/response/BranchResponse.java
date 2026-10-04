package com.restaurant.erp.branch.dto.response;

import com.restaurant.erp.branch.entity.emuns.BranchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO Response trả về thông tin chi nhánh nhà hàng")
public class BranchResponse {

    @Schema(description = "ID chi nhánh", example = "1")
    private Integer id;

    @Schema(description = "Mã chi nhánh", example = "CN01")
    private String code;

    @Schema(description = "Tên chi nhánh", example = "Chi nhánh Landmark 81")
    private String name;

    @Schema(description = "Địa chỉ", example = "720A Điện Biên Phủ, Phường 22, Bình Thạnh, TP.HCM")
    private String address;

    @Schema(description = "Số điện thoại", example = "02838129999")
    private String phone;

    @Schema(description = "Email", example = "landmark@restaurant.vn")
    private String email;

    @Schema(description = "Mã số thuế", example = "0315891234-001")
    private String taxCode;

    @Schema(description = "Giờ mở cửa", example = "08:00:00")
    private LocalTime openingTime;

    @Schema(description = "Giờ đóng cửa", example = "22:30:00")
    private LocalTime closingTime;

    @Schema(description = "Ảnh chi nhánh")
    private String image;

    @Schema(description = "Trạng thái", example = "ACTIVE")
    private BranchStatus status;

    @Schema(description = "Trạng thái hoạt động", example = "true")
    private Boolean isActive;

    @Schema(description = "Tên người quản lý", example = "Nguyễn Văn A")
    private String managerName;

    @Schema(description = "Tổng số bàn", example = "25")
    private Integer totalTables;

    @Schema(description = "Thời điểm tạo")
    private java.time.ZonedDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật gần nhất")
    private java.time.ZonedDateTime updatedAt;
}
