package com.restaurant.erp.reservation.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.reservation.dto.request.CreateBookingRequest;
import com.restaurant.erp.reservation.dto.response.BookingResponse;
import com.restaurant.erp.reservation.entity.Booking.BookingStatus;
import com.restaurant.erp.reservation.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reservation Management", description = "APIs for table booking and reservation management")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/api/bookings")
    @Operation(summary = "Lấy danh sách các lượt đặt bàn")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookings() {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingResponses()));
    }

    @PostMapping("/api/bookings")
    @Operation(summary = "Tạo lượt đặt bàn mới với validation")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.createBooking(request), "Đặt bàn thành công"));
    }

    @PutMapping("/api/bookings/{id}/status")
    @Operation(summary = "Cập nhật trạng thái đặt bàn (PENDING, CONFIRMED, SEATED, CANCELLED)")
    public ResponseEntity<ApiResponse<BookingResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.updateBookingStatus(id, status), "Cập nhật trạng thái đặt bàn thành công"));
    }
}
