package com.restaurant.erp.table.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Thông tin chi tiết khu vực bàn ăn")
public class AreaResponse {

    @Schema(description = "ID khu vực", example = "1")
    private Integer id;

    @Schema(description = "ID chi nhánh trực thuộc", example = "1")
    private Integer branchId;

    @Schema(description = "Tên khu vực", example = "Tầng 1 - Sân Vườn")
    private String name;

    @Schema(description = "Số lượng bàn ăn thuộc khu vực", example = "12")
    private Integer tableCount;

    @Schema(description = "Thời gian tạo khu vực")
    private ZonedDateTime createdAt;
}
