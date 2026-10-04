package com.restaurant.erp.user.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.erp.user.entity.User.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@Schema(description = "Yêu cầu cập nhật thông tin tài khoản người dùng")
public class UpdateUserRequest {

    @Schema(description = "ID chi nhánh trực thuộc", example = "1")
    private Integer branchId;

    @Schema(description = "Họ và tên", example = "Nguyễn Văn A", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ và tên phải từ 2 đến 100 ký tự")
    private String fullName;

    @Schema(description = "Số điện thoại", example = "0912345678")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không đúng định dạng")
    private String phoneNumber;

    @Schema(description = "Vai trò người dùng trong hệ thống", example = "CASHIER")
    private UserRole role;

    @Schema(description = "Mã PIN thao tác POS", example = "1234")
    @Pattern(regexp = "^[0-9]{4,6}$", message = "Mã PIN phải từ 4 đến 6 chữ số")
    private String pinCode;

    @Schema(description = "Trạng thái hoạt động", example = "true")
    private Boolean isActive;
}
