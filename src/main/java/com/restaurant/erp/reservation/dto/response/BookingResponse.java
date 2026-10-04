package com.restaurant.erp.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.erp.reservation.entity.Booking.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Thông tin chi tiết lượt đặt bàn")
public class BookingResponse {

    @Schema(description = "ID phiếu đặt bàn", example = "1")
    private Long id;

    @Schema(description = "ID chi nhánh", example = "1")
    private Integer branchId;

    @Schema(description = "ID khách hàng (nếu có)", example = "10")
    private Long customerId;

    @Schema(description = "ID bàn ăn", example = "5")
    private Integer tableId;

    @Schema(description = "Số hiệu / tên bàn ăn", example = "Bàn 05")
    private String tableNumber;

    @Schema(description = "Tên khách hàng", example = "Nguyễn Văn A")
    private String customerName;

    @Schema(description = "Số điện thoại", example = "0912345678")
    private String customerPhone;

    @Schema(description = "Thời gian nhận bàn")
    private ZonedDateTime bookingTime;

    @Schema(description = "Số lượng khách tham gia", example = "4")
    private Integer partySize;

    @Schema(description = "Số tiền cọc", example = "200000")
    private BigDecimal depositAmount;

    @Schema(description = "Trạng thái đặt bàn", example = "CONFIRMED")
    private BookingStatus status;

    @Schema(description = "Yêu cầu đặc biệt", example = "Ghế trẻ em")
    private String specialRequest;

    @Schema(description = "Thời gian đặt phiếu")
    private ZonedDateTime createdAt;
}
