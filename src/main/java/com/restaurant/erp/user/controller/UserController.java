package com.restaurant.erp.user.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.user.dto.UserDto;
import com.restaurant.erp.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/auth/login")
    public ResponseEntity<ApiResponse<UserDto.AuthResponse>> login(@Valid @RequestBody UserDto.AuthRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.login(request), "Login successful"));
    }

    @PostMapping("/api/auth/refresh")
    public ResponseEntity<ApiResponse<UserDto.AuthResponse>> refreshToken(@RequestBody UserDto.RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.refreshToken(request.getRefreshToken()), "Token refreshed successfully"));
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody UserDto.RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.register(request), "User registered successfully"));
    }

    @GetMapping("/api/users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
    }

    @GetMapping("/api/users/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @PutMapping("/api/users/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDto dto) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(id, dto), "User updated successfully"));
    }
}
