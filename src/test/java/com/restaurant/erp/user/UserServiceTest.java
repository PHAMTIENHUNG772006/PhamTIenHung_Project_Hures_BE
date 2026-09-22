package com.restaurant.erp.user;

import com.restaurant.erp.user.dto.UserDto;
import com.restaurant.erp.user.entity.User;
import com.restaurant.erp.user.repository.UserRepository;
import com.restaurant.erp.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void testLoginWithUsername() {
        UserDto.AuthRequest req = UserDto.AuthRequest.builder()
                .username("admin")
                .password("123456")
                .build();
        UserDto.AuthResponse res = userService.login(req);
        assertNotNull(res);
        assertNotNull(res.getToken());
        assertNotNull(res.getUser());
        assertEquals("admin@restaurant.com", res.getUser().getUsername());
        assertEquals(User.UserRole.ADMIN, res.getUser().getRole());
        System.out.println(">>> LOGIN WITH USERNAME ADMIN SUCCESS: " + res.getUser().getFullName());
    }

    @Test
    void testLoginWithEmailField() {
        UserDto.AuthRequest req = UserDto.AuthRequest.builder()
                .email("admin@restaurant.com")
                .password("123456")
                .build();
        UserDto.AuthResponse res = userService.login(req);
        assertNotNull(res);
        assertNotNull(res.getToken());
        assertEquals(User.UserRole.ADMIN, res.getUser().getRole());
        System.out.println(">>> LOGIN WITH EMAIL FIELD SUCCESS: " + res.getUser().getUsername());
    }

    @Test
    void testLoginManager() {
        UserDto.AuthRequest req = UserDto.AuthRequest.builder()
                .username("manager")
                .password("123456")
                .build();
        UserDto.AuthResponse res = userService.login(req);
        assertNotNull(res);
        assertEquals(User.UserRole.MANAGER, res.getUser().getRole());
        System.out.println(">>> LOGIN MANAGER SUCCESS: role=" + res.getUser().getRole());
    }

    @Test
    void testRefreshToken() {
        UserDto.AuthRequest req = UserDto.AuthRequest.builder()
                .username("admin")
                .password("123456")
                .build();
        UserDto.AuthResponse res = userService.login(req);
        assertNotNull(res.getToken());

        UserDto.AuthResponse refreshRes = userService.refreshToken(res.getToken());
        assertNotNull(refreshRes);
        assertNotNull(refreshRes.getToken());
        System.out.println(">>> REFRESH TOKEN SUCCESS");
    }
}
