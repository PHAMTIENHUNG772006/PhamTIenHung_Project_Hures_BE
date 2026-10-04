package com.restaurant.erp.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.erp.user.entity.User.UserRole;
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
@Schema(description = "Thông tin chi tiết người dùng")
public class UserResponse {

    @Schema(description = "ID người dùng", example = "1")
    private Long id;

    @Schema(description = "ID chi nhánh trực thuộc", example = "1")
    private Integer branchId;

    @Schema(description = "Tên chi nhánh", example = "Chi nhánh Cầu Giấy")
    private String branchName;

    @Schema(description = "Họ và tên", example = "Nguyễn Văn A")
    private String fullName;

    @Schema(description = "Tên tài khoản", example = "admin")
    private String username;

    @Schema(description = "Email", example = "admin@restaurant.com")
    private String email;

    @Schema(description = "Số điện thoại", example = "0912345678")
    private String phoneNumber;

    @Schema(description = "Vai trò", example = "ADMIN")
    private UserRole role;

    @Schema(description = "Trạng thái kích hoạt", example = "true")
    private Boolean isActive;

    @Schema(description = "Trạng thái hiển thị", example = "ACTIVE")
    private String status;

    @Schema(description = "Thời điểm tạo tài khoản")
    private ZonedDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật gần nhất")
    private ZonedDateTime updatedAt;
}
