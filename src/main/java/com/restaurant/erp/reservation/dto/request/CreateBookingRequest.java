package com.restaurant.erp.reservation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Yêu cầu đặt bàn mới")
public class CreateBookingRequest {

    @Schema(description = "ID chi nhánh đặt bàn (nếu không truyền sẽ lấy theo header)")
    private Integer branchId;

    @Schema(description = "ID khách hàng (nếu là khách đã có tài khoản)")
    private Long customerId;

    @Schema(description = "ID bàn ăn chọn đặt", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Vui lòng chọn bàn ăn")
    private Integer tableId;

    @Schema(description = "Tên khách hàng liên hệ", example = "Nguyễn Văn A", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Tên khách hàng không được để trống")
    private String customerName;

    @Schema(description = "Số điện thoại liên hệ", example = "0912345678", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không hợp lệ (gồm 10-11 số)")
    private String customerPhone;

    @Schema(description = "Thời gian nhận bàn", example = "2026-10-02T19:00:00+07:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Thời gian đặt bàn không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime bookingTime;

    @Schema(description = "Số lượng khách tham gia", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Số lượng khách không được để trống")
    @Min(value = 1, message = "Số lượng khách tối thiểu là 1 người")
    private Integer partySize;

    @Schema(description = "Số tiền đặt cọc trước (nếu có)", example = "200000")
    @DecimalMin(value = "0.0", message = "Tiền đặt cọc không được âm")
    @Builder.Default
    private BigDecimal depositAmount = BigDecimal.ZERO;

    @Schema(description = "Ghi chú hoặc yêu cầu đặc biệt của khách hàng", example = "Bàn cạnh cửa sổ, chuẩn bị ghế trẻ em")
    private String specialRequest;
}
