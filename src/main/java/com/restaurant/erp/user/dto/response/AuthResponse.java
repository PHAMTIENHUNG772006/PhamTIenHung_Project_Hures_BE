package com.restaurant.erp.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Kết quả xác thực đăng nhập hoặc refresh token")
public class AuthResponse {

    @Schema(description = "JWT Access Token dùng để chứng thực API")
    private String token;

    @Schema(description = "JWT Refresh Token dùng để làm mới phiên đăng nhập")
    private String refreshToken;

    @Schema(description = "Thông tin tài khoản đã đăng nhập")
    private UserResponse user;
}
