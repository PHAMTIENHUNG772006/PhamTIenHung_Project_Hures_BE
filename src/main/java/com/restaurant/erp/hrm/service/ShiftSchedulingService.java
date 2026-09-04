package com.restaurant.erp.hrm.service;

import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.repository.BranchRepository;
import com.restaurant.erp.common.context.BranchContext;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.hrm.dto.WorkShiftDto;
import com.restaurant.erp.hrm.entity.WorkShift;
import com.restaurant.erp.hrm.repository.WorkShiftRepository;
import com.restaurant.erp.user.entity.User;
import com.restaurant.erp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftSchedulingService {

    private final WorkShiftRepository workShiftRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;

    private Integer getActiveBranchId() {
        Integer branchId = BranchContext.getCurrentBranchId();
        if (branchId == null) {
            throw new RuntimeException("Branch context is not set");
        }
        return branchId;
    }

    public List<WorkShiftDto> getShiftsForDate(LocalDate date) {
        Integer branchId = getActiveBranchId();
        List<WorkShift> shifts = (date != null)
                ? workShiftRepository.findByBranchIdAndShiftDate(branchId, date)
                : workShiftRepository.findByBranchId(branchId);
        return shifts.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public WorkShiftDto scheduleShift(WorkShiftDto dto) {
        Branch branch = branchRepository.findById(getActiveBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        WorkShift shift = WorkShift.builder()
                .branch(branch)
                .user(user)
                .shiftDate(dto.getShiftDate())
                .scheduledStart(dto.getScheduledStart())
                .scheduledEnd(dto.getScheduledEnd())
                .status(WorkShift.ShiftStatus.SCHEDULED)
                .createdAt(ZonedDateTime.now())
                .build();

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