package com.restaurant.erp.hrm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInRequest {

    @NotNull(message = "ID nhân viên không được để trống")
    private Long userId;

    private String method;
}
