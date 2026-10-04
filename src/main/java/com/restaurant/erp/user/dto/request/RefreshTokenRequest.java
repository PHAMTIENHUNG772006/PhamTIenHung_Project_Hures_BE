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
@Schema(description = "Yêu cầu cấp mới access token bằng refresh token")
public class RefreshTokenRequest {

    @Schema(description = "Refresh Token đã được cấp khi đăng nhập", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Refresh token không được để trống")
    private String refreshToken;
}
