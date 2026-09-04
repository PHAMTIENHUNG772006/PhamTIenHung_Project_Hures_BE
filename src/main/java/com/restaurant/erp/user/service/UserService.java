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
import java.util.UUID;
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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwtToken = jwtProvider.generateToken(userDetails);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserDto.AuthResponse.builder()
                .token(jwtToken)
                .user(mapToDto(user))
                .build();
    }

    @Transactional
    public UserDto register(UserDto.RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Email already exists: " + request.getEmail());
        }

        Branch branch = null;
        if (request.getBranchId() != null) {
            branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + request.getBranchId()));
        }

        User user = User.builder()
                .branch(branch)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole() != null ? request.getRole() : User.UserRole.CUSTOMER)
                .pinCode(request.getPinCode())
                .isActive(true)
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

    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToDto(user);
    }

    @Transactional
    public UserDto updateUser(UUID id, UserDto dto) {
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
        return UserDto.builder()
                .id(user.getId())
                .branchId(user.getBranch() != null ? user.getBranch().getId() : null)
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .pinCode(user.getPinCode())
                .isActive(user.getIsActive())
                .build();
    }
}
