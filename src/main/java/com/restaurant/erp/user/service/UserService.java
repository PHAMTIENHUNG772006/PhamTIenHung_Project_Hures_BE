package com.restaurant.erp.user.service;

import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.repository.BranchRepository;
import com.restaurant.erp.common.exception.BusinessException;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.security.CustomUserDetailsService;
import com.restaurant.erp.security.jwt.JwtProvider;
import com.restaurant.erp.user.dto.UserDto;
import com.restaurant.erp.user.entity.User;
import com.restaurant.erp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;

    public UserDto.AuthResponse login(UserDto.AuthRequest request) {
        String rawIdentifier = request.getUsername();
        if (rawIdentifier == null || rawIdentifier.isBlank()) {
            rawIdentifier = request.getEmail();
        }
        if (rawIdentifier == null || rawIdentifier.isBlank()) {
            throw new BusinessException("Vui lòng nhập tên đăng nhập hoặc email");
        }
        final String identifier = rawIdentifier.trim();

        User user = userRepository.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại: " + identifier));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String jwtToken = jwtProvider.generateToken(userDetails);

        return UserDto.AuthResponse.builder()
                .token(jwtToken)
                .user(mapToDto(user))
                .build();
    }

    public UserDto.AuthResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessException("Refresh token is required");
        }
        String username = jwtProvider.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtProvider.validateToken(refreshToken, userDetails)) {
            throw new BusinessException("Invalid or expired refresh token");
        }
        String newToken = jwtProvider.generateToken(userDetails);
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return UserDto.AuthResponse.builder()
                .token(newToken)
                .user(mapToDto(user))
                .build();
    }

    @Transactional
    public UserDto register(UserDto.RegisterRequest request) {
        String rawIdentifier = request.getUsername();
        if (rawIdentifier == null || rawIdentifier.isBlank()) {
            rawIdentifier = request.getEmail();
        }
        if (rawIdentifier == null || rawIdentifier.isBlank()) {
            throw new BusinessException("Vui lòng nhập tên đăng nhập hoặc email");
        }
        final String identifier = rawIdentifier.trim();

        if (userRepository.findByUsernameOrEmail(identifier).isPresent()) {
            throw new BusinessException("Tên đăng nhập đã tồn tại: " + identifier);
        }

        Branch branch = null;
        if (request.getBranchId() != null) {
            branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + request.getBranchId()));
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .branch(branch)
                .fullName(request.getFullName())
                .username(identifier)
                .password(encodedPassword)
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole() != null ? request.getRole() : User.UserRole.CUSTOMER)
                .pinCode(request.getPinCode())
                .isActive(true)
                .status("ACTIVE")
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        return mapToDto(userRepository.save(user));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPinCode(dto.getPinCode());
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        if (dto.getIsActive() != null) {
            user.setIsActive(dto.getIsActive());
            user.setStatus(dto.getIsActive() ? "ACTIVE" : "INACTIVE");
        }
        user.setUpdatedAt(ZonedDateTime.now());

        if (dto.getBranchId() != null) {
            Branch branch = branchRepository.findById(dto.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + dto.getBranchId()));
            user.setBranch(branch);
        }

        return mapToDto(userRepository.save(user));
    }

    public UserDto mapToDto(User user) {
        if (user == null) return null;
        String email = user.getUsername().contains("@") ? user.getUsername() : (user.getUsername() + "@restaurant.com");
        return UserDto.builder()
                .id(user.getId())
                .branchId(user.getBranch() != null ? user.getBranch().getId() : 1)
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(email)
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .pinCode(user.getPinCode())
                .isActive(user.getIsActive())
                .status(user.getStatus())
                .build();
    }
}
