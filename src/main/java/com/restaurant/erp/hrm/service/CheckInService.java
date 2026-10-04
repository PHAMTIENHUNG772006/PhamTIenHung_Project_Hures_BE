package com.restaurant.erp.hrm.service;

import com.restaurant.erp.common.exception.BusinessException;
import com.restaurant.erp.hrm.dto.WorkShiftDto;
import com.restaurant.erp.hrm.dto.response.WorkShiftResponse;
import com.restaurant.erp.hrm.entity.WorkShift;
import com.restaurant.erp.hrm.repository.WorkShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final WorkShiftRepository workShiftRepository;

    @Transactional
    public WorkShiftResponse checkInResponse(Long userId, String method) {
        LocalDate today = LocalDate.now();
        WorkShift shift = workShiftRepository.findByUserIdAndShiftDateAndStatus(userId, today, WorkShift.ShiftStatus.SCHEDULED)
                .orElseThrow(() -> new BusinessException("No scheduled shift found for user today to check in"));

        shift.setActualCheckIn(ZonedDateTime.now());
        shift.setStatus(WorkShift.ShiftStatus.CHECKED_IN);
        shift.setCheckInMethod(method != null ? method : "FACE_ID");

        return mapToResponse(workShiftRepository.save(shift));
    }

    @Transactional
    public WorkShiftResponse checkOutResponse(Long userId) {
        LocalDate today = LocalDate.now();
        WorkShift shift = workShiftRepository.findByUserIdAndShiftDateAndStatus(userId, today, WorkShift.ShiftStatus.CHECKED_IN)
                .orElseThrow(() -> new BusinessException("No active checked-in shift found for user today to check out"));

        shift.setActualCheckOut(ZonedDateTime.now());
        shift.setStatus(WorkShift.ShiftStatus.CHECKED_OUT);

        return mapToResponse(workShiftRepository.save(shift));
    }

    @Transactional
    public WorkShiftDto checkIn(Long userId, String method) {
        return mapToDto(checkInResponse(userId, method));
    }

    @Transactional
    public WorkShiftDto checkOut(Long userId) {
        return mapToDto(checkOutResponse(userId));
    }

    private WorkShiftResponse mapToResponse(WorkShift ws) {
        return WorkShiftResponse.builder()
                .id(ws.getId())
                .branchId(ws.getBranch().getId())
                .userId(ws.getUser().getId())
                .userFullName(ws.getUser().getFullName())
                .shiftDate(ws.getShiftDate())
                .scheduledStart(ws.getScheduledStart())
                .scheduledEnd(ws.getScheduledEnd())
                .actualCheckIn(ws.getActualCheckIn())
                .actualCheckOut(ws.getActualCheckOut())
                .status(ws.getStatus())
                .checkInMethod(ws.getCheckInMethod())
                .build();
    }

    private WorkShiftDto mapToDto(WorkShiftResponse r) {
        return WorkShiftDto.builder()
                .id(r.getId())
                .branchId(r.getBranchId())
                .userId(r.getUserId())
                .userFullName(r.getUserFullName())
                .shiftDate(r.getShiftDate())
                .scheduledStart(r.getScheduledStart())
                .scheduledEnd(r.getScheduledEnd())
                .actualCheckIn(r.getActualCheckIn())
                .actualCheckOut(r.getActualCheckOut())
                .status(r.getStatus())
                .checkInMethod(r.getCheckInMethod())
                .build();
    }
}