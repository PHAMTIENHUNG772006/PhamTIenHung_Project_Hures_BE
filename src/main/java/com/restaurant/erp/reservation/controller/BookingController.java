package com.restaurant.erp.reservation.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.reservation.dto.BookingDto;
import com.restaurant.erp.reservation.entity.Booking.BookingStatus;
import com.restaurant.erp.reservation.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/api/bookings")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getBookings() {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBookings()));
    }

    @PostMapping("/api/bookings")
    public ResponseEntity<ApiResponse<BookingDto>> createBooking(@Valid @RequestBody BookingDto dto) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.createBooking(dto), "Booking created successfully"));
    }

    @PutMapping("/api/bookings/{id}/status")
    public ResponseEntity<ApiResponse<BookingDto>> updateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.updateStatus(id, status), "Booking status updated successfully"));
    }
}
