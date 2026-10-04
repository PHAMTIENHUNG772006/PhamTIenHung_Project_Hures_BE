package com.restaurant.erp.user.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.erp.user.entity.User.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
@Schema(description = "Yêu cầu đăng ký tài khoản người dùng")
public class RegisterRequest {

    @Schema(description = "ID chi nhánh làm việc", example = "1")
    private Integer branchId;

    @Schema(description = "Họ và tên đầy đủ", example = "Nguyễn Văn A", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ và tên phải từ 2 đến 100 ký tự")
    private String fullName;

    @Schema(description = "Tên đăng nhập", example = "staff_01")
    private String username;

    @Schema(description = "Email người dùng", example = "staff01@restaurant.com")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @Schema(description = "Mật khẩu", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu tối thiểu phải từ 6 ký tự")
    private String password;

    @Schema(description = "Số điện thoại liên hệ", example = "0912345678")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại phải từ 10-11 chữ số hợp lệ")
    private String phoneNumber;

    @Schema(description = "Vai trò người dùng trong hệ thống", example = "WAITER")
    private UserRole role;

    @Schema(description = "Mã PIN phục vụ POS/KDS nhanh", example = "1234")
    @Pattern(regexp = "^[0-9]{4,6}$", message = "Mã PIN phải từ 4 đến 6 chữ số")
    private String pinCode;

    public String getUsername() {
        if (username != null && !username.isBlank()) {
            return username.trim();
        }
        return email != null ? email.trim() : null;
    }
}
