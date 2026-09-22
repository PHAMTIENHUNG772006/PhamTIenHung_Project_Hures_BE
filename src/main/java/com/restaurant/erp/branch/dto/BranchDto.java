package com.restaurant.erp.branch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.erp.branch.entity.emuns.BranchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BranchDto {
    private Integer id;
    private String code;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String taxCode;

    @JsonFormat(pattern = "HH:mm[:ss]")
    private LocalTime openingTime;

    @JsonFormat(pattern = "HH:mm[:ss]")
    private LocalTime closingTime;

    private String image;
    private BranchStatus status;
    private Boolean isActive;
    private String managerName;
    private Integer totalTables;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}