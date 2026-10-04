package com.restaurant.erp.hrm.service;

import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.repository.BranchRepository;
import com.restaurant.erp.common.context.BranchContext;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.hrm.dto.WorkShiftDto;
import com.restaurant.erp.hrm.dto.request.ScheduleShiftRequest;
import com.restaurant.erp.hrm.dto.response.WorkShiftResponse;
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

    public List<WorkShiftResponse> getShiftResponsesForDate(LocalDate date) {
        Integer branchId = getActiveBranchId();
        List<WorkShift> shifts = (date != null)
                ? workShiftRepository.findByBranchIdAndShiftDate(branchId, date)
                : workShiftRepository.findByBranchId(branchId);
        return shifts.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<WorkShiftDto> getShiftsForDate(LocalDate date) {
        Integer branchId = getActiveBranchId();
        List<WorkShift> shifts = (date != null)
                ? workShiftRepository.findByBranchIdAndShiftDate(branchId, date)
                : workShiftRepository.findByBranchId(branchId);
        return shifts.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public WorkShiftResponse scheduleShift(ScheduleShiftRequest request) {
        Integer branchId = request.getBranchId() != null ? request.getBranchId() : getActiveBranchId();
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        WorkShift shift = WorkShift.builder()
                .branch(branch)
                .user(user)
                .shiftDate(request.getShiftDate())
                .scheduledStart(request.getScheduledStart())
                .scheduledEnd(request.getScheduledEnd())
                .status(WorkShift.ShiftStatus.SCHEDULED)
                .createdAt(ZonedDateTime.now())
                .build();

        return mapToResponse(workShiftRepository.save(shift));
    }

    @Transactional
    public WorkShiftDto scheduleShift(WorkShiftDto dto) {
        ScheduleShiftRequest req = ScheduleShiftRequest.builder()
                .branchId(dto.getBranchId())
                .userId(dto.getUserId())
                .shiftDate(dto.getShiftDate())
                .scheduledStart(dto.getScheduledStart())
                .scheduledEnd(dto.getScheduledEnd())
                .build();
        WorkShiftResponse res = scheduleShift(req);
        return mapResponseToDto(res);
    }

    public WorkShiftResponse mapToResponse(WorkShift ws) {
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

    private WorkShiftDto mapToDto(WorkShift ws) {
        return mapResponseToDto(mapToResponse(ws));
    }

    private WorkShiftDto mapResponseToDto(WorkShiftResponse r) {
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