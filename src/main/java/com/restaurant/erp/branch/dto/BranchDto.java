package com.restaurant.erp.branch.dto;

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
public class BranchDto {
    private Integer id;
    private String code;
    private String name;
    private String address;
    private String phone;
    private Boolean isActive;
}
