package com.restaurant.erp.user.dto;

import com.restaurant.erp.user.entity.User.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private Integer branchId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private String pinCode;
    private Boolean isActive;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthRequest {
        private String email;
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthResponse {
        private String token;
        private UserDto user;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegisterRequest {
        private Integer branchId;
        private String fullName;
        private String email;
        private String password;
        private String phoneNumber;
        private UserRole role;
        private String pinCode;
    }
}
