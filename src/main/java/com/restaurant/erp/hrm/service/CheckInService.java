package com.restaurant.erp.hrm.service;

import com.restaurant.erp.common.exception.BusinessException;
import com.restaurant.erp.hrm.dto.WorkShiftDto;
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
    public WorkShiftDto checkIn(Long userId, String method) {
        LocalDate today = LocalDate.now();
        WorkShift shift = workShiftRepository.findByUserIdAndShiftDateAndStatus(userId, today, WorkShift.ShiftStatus.SCHEDULED)
                .orElseThrow(() -> new BusinessException("No scheduled shift found for user today to check in"));

        shift.setActualCheckIn(ZonedDateTime.now());
        shift.setStatus(WorkShift.ShiftStatus.CHECKED_IN);
        shift.setCheckInMethod(method);

        return mapToDto(workShiftRepository.save(shift));
    }

    @Transactional
    public WorkShiftDto checkOut(Long userId) {
        LocalDate today = LocalDate.now();
        WorkShift shift = workShiftRepository.findByUserIdAndShiftDateAndStatus(userId, today, WorkShift.ShiftStatus.CHECKED_IN)
                .orElseThrow(() -> new BusinessException("No active checked-in shift found for user today to check out"));

        shift.setActualCheckOut(ZonedDateTime.now());
        shift.setStatus(WorkShift.ShiftStatus.CHECKED_OUT);

        return mapToDto(workShiftRepository.save(shift));
    }

    private WorkShiftDto mapToDto(WorkShift ws) {
        return WorkShiftDto.builder()
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
}