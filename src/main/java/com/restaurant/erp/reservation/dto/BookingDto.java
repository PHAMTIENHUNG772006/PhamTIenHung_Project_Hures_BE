package com.restaurant.erp.reservation.dto;

import com.restaurant.erp.reservation.entity.Booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private Integer branchId;
    private UUID customerId;
    private Integer tableId;
    private String tableNumber;
    private String customerName;
    private String customerPhone;
    private ZonedDateTime bookingTime;
    private Integer partySize;
    private BigDecimal depositAmount;
    private BookingStatus status;
    private String specialRequest;
}
