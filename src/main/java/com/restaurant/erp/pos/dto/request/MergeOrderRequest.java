package com.restaurant.erp.pos.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu gộp hóa đơn vào đơn hàng đích")
public class MergeOrderRequest {

    @Schema(description = "ID đơn hàng đích nhận gộp", example = "102", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Mã đơn hàng đích không được để trống")
    private Long destinationOrderId;
}
