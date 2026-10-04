package com.restaurant.erp.user.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu đăng nhập tài khoản")
public class LoginRequest {

    @Schema(description = "Tên đăng nhập hoặc Email", example = "admin")
    private String username;

    @Schema(description = "Email đăng nhập (nếu không dùng username)", example = "admin@restaurant.com")
    private String email;

    @Schema(description = "Mật khẩu đăng nhập", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    public String getIdentifier() {
        if (username != null && !username.isBlank()) {
            return username.trim();
        }
        if (email != null && !email.isBlank()) {
            return email.trim();
        }
        return null;
    }
}
