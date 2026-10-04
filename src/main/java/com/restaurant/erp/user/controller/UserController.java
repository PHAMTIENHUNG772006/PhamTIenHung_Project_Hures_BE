package com.restaurant.erp.user.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.user.dto.request.LoginRequest;
import com.restaurant.erp.user.dto.request.RefreshTokenRequest;
import com.restaurant.erp.user.dto.request.RegisterRequest;
import com.restaurant.erp.user.dto.request.UpdateUserRequest;
import com.restaurant.erp.user.dto.response.AuthResponse;
import com.restaurant.erp.user.dto.response.UserResponse;
import com.restaurant.erp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "User & Authentication", description = "APIs for user auth and management")
public class UserController {

    private final UserService userService;

    @PostMapping("/api/auth/login")
    @Operation(summary = "Đăng nhập tài khoản")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.loginUser(request), "Đăng nhập thành công"));
    }

    @PostMapping("/api/auth/refresh")
    @Operation(summary = "Làm mới Access Token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.refreshTokenUser(request), "Cấp mới token thành công"));
    }

    @PostMapping("/api/auth/register")
    @Operation(summary = "Đăng ký tài khoản người dùng")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.registerUser(request), "Đăng ký người dùng thành công"));
    }

    @GetMapping("/api/users")
    @Operation(summary = "Lấy danh sách người dùng")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUserResponses()));
    }

    @GetMapping("/api/users/{id}")
    @Operation(summary = "Lấy chi tiết người dùng theo ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserResponseById(id)));
    }

    @PutMapping("/api/users/{id}")
    @Operation(summary = "Cập nhật thông tin người dùng")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(id, request), "Cập nhật thông tin người dùng thành công"));
    }
}
