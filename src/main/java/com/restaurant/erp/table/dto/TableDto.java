package com.restaurant.erp.table.dto;

import com.restaurant.erp.table.entity.DiningTable.TableStatus;
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
public class TableDto {
    private Integer id;
    private Integer branchId;
    private Integer areaId;
    private String areaName;
    private String tableNumber;
    private Integer capacity;
    private TableStatus status;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AreaDto {
        private Integer id;
        private Integer branchId;
        private String name;
    }
}
