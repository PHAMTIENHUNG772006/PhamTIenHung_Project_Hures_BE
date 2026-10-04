package com.restaurant.erp.validation;

import com.restaurant.erp.branch.dto.request.BranchCreateRequest;
import com.restaurant.erp.hrm.dto.request.ScheduleShiftRequest;
import com.restaurant.erp.inventory.dto.request.CreateStockTransactionRequest;
import com.restaurant.erp.menu.dto.request.CreateMenuItemRequest;
import com.restaurant.erp.pos.dto.request.ProcessPaymentRequest;
import com.restaurant.erp.reservation.dto.request.CreateBookingRequest;
import com.restaurant.erp.table.dto.request.CreateTableRequest;
import com.restaurant.erp.user.dto.request.LoginRequest;
import com.restaurant.erp.user.dto.request.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeanValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validate BranchCreateRequest: phát hiện vi phạm khi để trống thông tin bắt buộc")
    void testBranchCreateRequestValidation() {
        BranchCreateRequest invalid = BranchCreateRequest.builder().build();
        Set<ConstraintViolation<BranchCreateRequest>> violations = validator.validate(invalid);
        assertFalse(violations.isEmpty(), "Cần có lỗi validation khi request rỗng");
    }

    @Test
    @DisplayName("Validate LoginRequest: kiểm tra password không được trống")
    void testLoginRequestValidation() {
        LoginRequest invalid = LoginRequest.builder().password("").build();
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(invalid);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));

        LoginRequest valid = LoginRequest.builder().username("admin").password("Secret123").build();
        Set<ConstraintViolation<LoginRequest>> validViolations = validator.validate(valid);
        assertTrue(validViolations.isEmpty());
    }

    @Test
    @DisplayName("Validate RegisterRequest: kiểm tra email, họ tên, password")
    void testRegisterRequestValidation() {
        RegisterRequest invalid = RegisterRequest.builder().email("invalid-email").password("").fullName("").build();
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(invalid);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fullName")));
    }

    @Test
    @DisplayName("Validate CreateTableRequest: kiểm tra tên bàn, khu vực và sức chứa")
    void testCreateTableRequestValidation() {
        CreateTableRequest invalid = CreateTableRequest.builder()
                .name("")
                .capacity(0)
                .build();
        Set<ConstraintViolation<CreateTableRequest>> violations = validator.validate(invalid);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("capacity")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("areaId")));
    }

    @Test
    @DisplayName("Validate CreateMenuItemRequest: kiểm tra tên món và giá bán dương")
    void testCreateMenuItemRequestValidation() {
        CreateMenuItemRequest invalid = CreateMenuItemRequest.builder()
                .name("")
                .price(BigDecimal.valueOf(-1000))
                .build();
        Set<ConstraintViolation<CreateMenuItemRequest>> violations = validator.validate(invalid);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @DisplayName("Validate ProcessPaymentRequest: kiểm tra hình thức thanh toán và số tiền")
    void testProcessPaymentRequestValidation() {
        ProcessPaymentRequest invalid = ProcessPaymentRequest.builder()
                .amount(BigDecimal.valueOf(-10))
                .method(null)
                .build();
        Set<ConstraintViolation<ProcessPaymentRequest>> violations = validator.validate(invalid);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("method")));
    }

    @Test
    @DisplayName("Validate CreateBookingRequest: kiểm tra tên khách và số điện thoại")
    void testCreateBookingRequestValidation() {
        CreateBookingRequest invalid = CreateBookingRequest.builder()
                .customerName("")
                .customerPhone("invalid-phone")
                .partySize(0)
                .build();
        Set<ConstraintViolation<CreateBookingRequest>> violations = validator.validate(invalid);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("customerName")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("partySize")));
    }

    @Test
    @DisplayName("Validate ScheduleShiftRequest: kiểm tra nhân viên và ngày làm")
    void testScheduleShiftRequestValidation() {
        ScheduleShiftRequest invalid = ScheduleShiftRequest.builder().build();
        Set<ConstraintViolation<ScheduleShiftRequest>> violations = validator.validate(invalid);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("userId")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("shiftDate")));
    }

    @Test
    @DisplayName("Validate CreateStockTransactionRequest: kiểm tra số lượng giao dịch kho")
    void testStockTransactionRequestValidation() {
        CreateStockTransactionRequest invalid = CreateStockTransactionRequest.builder()
                .quantity(-5.0)
                .build();
        Set<ConstraintViolation<CreateStockTransactionRequest>> violations = validator.validate(invalid);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("quantity")));
    }
}
