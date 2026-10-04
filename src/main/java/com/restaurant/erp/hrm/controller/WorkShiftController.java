package com.restaurant.erp.hrm.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.hrm.dto.request.ScheduleShiftRequest;
import com.restaurant.erp.hrm.dto.response.WorkShiftResponse;
import com.restaurant.erp.hrm.service.CheckInService;
import com.restaurant.erp.hrm.service.ShiftSchedulingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hrm/shifts")
@RequiredArgsConstructor
public class WorkShiftController {

    private final ShiftSchedulingService shiftSchedulingService;
    private final CheckInService checkInService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkShiftResponse>>> getShiftsForDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(shiftSchedulingService.getShiftResponsesForDate(date)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WorkShiftResponse>> scheduleShift(@Valid @RequestBody ScheduleShiftRequest request) {
        return ResponseEntity.ok(ApiResponse.success(shiftSchedulingService.scheduleShift(request), "Shift scheduled successfully"));
    }

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<WorkShiftResponse>> checkIn(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "FACE_ID") String method) {
        return ResponseEntity.ok(ApiResponse.success(checkInService.checkInResponse(userId, method), "Checked in successfully"));
    }

    @PostMapping("/check-out")
    public ResponseEntity<ApiResponse<WorkShiftResponse>> checkOut(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(checkInService.checkOutResponse(userId), "Checked out successfully"));
    }
}