package com.restaurant.erp.table.dto;

import com.restaurant.erp.table.entity.DiningTable.TableStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Vui lòng chọn khu vực trực thuộc")
    private Integer areaId;

    private String areaName;

    private String tableNumber;

    @NotBlank(message = "Tên bàn không được để trống")
    @Size(min = 1, max = 50, message = "Tên bàn phải từ 1 đến 50 ký tự")
    private String name;

    @NotNull(message = "Sức chứa bàn không được để trống")
    @Min(value = 1, message = "Sức chứa tối thiểu từ 1 khách trở lên")
    @Max(value = 100, message = "Sức chứa tối đa không vượt quá 100 khách")
    private Integer capacity;

    private TableStatus status;

    public String getName() {
        return name != null && !name.isEmpty() ? name : tableNumber;
    }

    public String getTableNumber() {
        return tableNumber != null && !tableNumber.isEmpty() ? tableNumber : name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AreaDto {
        private Integer id;
        private Integer branchId;

        @NotBlank(message = "Tên khu vực không được để trống")
        @Size(min = 2, max = 50, message = "Tên khu vực phải từ 2 đến 50 ký tự")
        private String name;
    }
}
