package com.restaurant.erp.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.erp.user.entity.User.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private Long id;
    private Integer branchId;
    private String fullName;
    private String username;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private String pinCode;
    private Boolean isActive;
    private String status;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthRequest {
        private String username;
        private String email;
        private String password;

        public String getUsername() {
            if (username != null && !username.isBlank()) {
                return username;
            }
            return email;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthResponse {
        private String token;
        private UserDto user;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RefreshTokenRequest {
        private String refreshToken;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RegisterRequest {
        private Integer branchId;
        private String fullName;
        private String username;
        private String email;
        private String password;
        private String phoneNumber;
        private UserRole role;
        private String pinCode;

        public String getUsername() {
            if (username != null && !username.isBlank()) {
                return username;
            }
            return email;
        }
    }
}
