package com.restaurant.erp.hrm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleShiftRequest {

    private Integer branchId;

    @NotNull(message = "ID nhân viên không được để trống")
    private Long userId;

    @NotNull(message = "Ngày làm việc không được để trống")
    private LocalDate shiftDate;

    @NotNull(message = "Giờ bắt đầu dự kiến không được để trống")
    private ZonedDateTime scheduledStart;

    @NotNull(message = "Giờ kết thúc dự kiến không được để trống")
    private ZonedDateTime scheduledEnd;
}
