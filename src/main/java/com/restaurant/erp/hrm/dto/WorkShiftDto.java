package com.restaurant.erp.hrm.dto;

import com.restaurant.erp.hrm.entity.WorkShift.ShiftStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkShiftDto {
    private Long id;
    private Integer branchId;
    private UUID userId;
    private String userFullName;
    private LocalDate shiftDate;
    private ZonedDateTime scheduledStart;
    private ZonedDateTime scheduledEnd;
    private ZonedDateTime actualCheckIn;
    private ZonedDateTime actualCheckOut;
    private ShiftStatus status;
    private String checkInMethod;
}
