package com.restaurant.erp.reservation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.erp.reservation.entity.Booking.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
@Schema(description = "Yêu cầu cập nhật thông tin đặt bàn")
public class UpdateBookingRequest {

    @Schema(description = "ID bàn ăn mới")
    private Integer tableId;

    @Schema(description = "Tên khách hàng liên hệ")
    private String customerName;

    @Schema(description = "Số điện thoại liên hệ")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không hợp lệ")
    private String customerPhone;

    @Schema(description = "Thời gian nhận bàn")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime bookingTime;

    @Schema(description = "Số lượng khách tham gia")
    @Min(value = 1, message = "Số lượng khách tối thiểu là 1 người")
    private Integer partySize;

    @Schema(description = "Số tiền đặt cọc")
    @DecimalMin(value = "0.0", message = "Tiền đặt cọc không được âm")
    private BigDecimal depositAmount;

    @Schema(description = "Trạng thái đặt bàn")
    private BookingStatus status;

    @Schema(description = "Yêu cầu đặc biệt")
    private String specialRequest;
}
