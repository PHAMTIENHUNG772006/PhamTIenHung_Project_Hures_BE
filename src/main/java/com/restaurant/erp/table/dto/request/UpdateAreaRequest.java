package com.restaurant.erp.table.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu cập nhật thông tin khu vực bàn ăn")
public class UpdateAreaRequest {

    @Schema(description = "Tên khu vực bàn ăn", example = "Tầng 1 - Sân Vườn", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Tên khu vực không được để trống")
    @Size(min = 2, max = 50, message = "Tên khu vực phải từ 2 đến 50 ký tự")
    private String name;
}
