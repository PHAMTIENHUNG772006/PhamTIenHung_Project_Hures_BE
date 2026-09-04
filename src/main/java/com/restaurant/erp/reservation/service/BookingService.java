package com.restaurant.erp.reservation.service;

import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.repository.BranchRepository;
import com.restaurant.erp.common.context.BranchContext;
import com.restaurant.erp.common.exception.BusinessException;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.reservation.dto.BookingDto;
import com.restaurant.erp.reservation.entity.Booking;
import com.restaurant.erp.reservation.repository.BookingRepository;
import com.restaurant.erp.table.entity.DiningTable;
import com.restaurant.erp.table.repository.TableRepository;
import com.restaurant.erp.user.entity.User;
import com.restaurant.erp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TableRepository tableRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final TableLockService tableLockService;

    private Integer getActiveBranchId() {
        Integer branchId = BranchContext.getCurrentBranchId();
        if (branchId == null) {
            throw new RuntimeException("Branch context is not set");
        }
        return branchId;
    }

    public List<BookingDto> getBookings() {
        return bookingRepository.findByBranchId(getActiveBranchId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto createBooking(BookingDto dto) {
        Integer branchId = getActiveBranchId();
        
        String bookingDateStr = dto.getBookingTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        boolean locked = tableLockService.lockTable(dto.getTableId(), bookingDateStr, 5, 10);
        if (!locked) {
            throw new BusinessException("Table is currently locked. Another reservation might be in progress.");
        }

        try {
            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
            DiningTable table = tableRepository.findById(dto.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Table not found"));
            User customer = null;
            if (dto.getCustomerId() != null) {
                customer = userRepository.findById(dto.getCustomerId()).orElse(null);
            }

            Booking booking = Booking.builder()
                    .branch(branch)
                    .table(table)
                    .customer(customer)
                    .customerName(dto.getCustomerName())
                    .customerPhone(dto.getCustomerPhone())
                    .bookingTime(dto.getBookingTime())
                    .partySize(dto.getPartySize())
                    .depositAmount(dto.getDepositAmount())
                    .status(Booking.BookingStatus.PENDING)
                    .specialRequest(dto.getSpecialRequest())
                    .build();

            return mapToDto(bookingRepository.save(booking));
        } finally {
            tableLockService.unlockTable(dto.getTableId(), bookingDateStr);
        }
    }

    @Transactional
    public BookingDto updateStatus(Long id, Booking.BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        booking.setStatus(status);
        
        if (status == Booking.BookingStatus.SEATED && booking.getTable() != null) {
            booking.getTable().setStatus(DiningTable.TableStatus.OCCUPIED);
            tableRepository.save(booking.getTable());
        } else if (status == Booking.BookingStatus.CANCELLED && booking.getTable() != null) {
            booking.getTable().setStatus(DiningTable.TableStatus.AVAILABLE);
            tableRepository.save(booking.getTable());
        }
        
        return mapToDto(bookingRepository.save(booking));
    }

    private BookingDto mapToDto(Booking booking) {
        if (booking == null) return null;
        return BookingDto.builder()
                .id(booking.getId())
                .branchId(booking.getBranch().getId())
                .customerId(booking.getCustomer() != null ? booking.getCustomer().getId() : null)
                .tableId(booking.getTable() != null ? booking.getTable().getId() : null)
                .tableNumber(booking.getTable() != null ? booking.getTable().getTableNumber() : null)
                .customerName(booking.getCustomerName())
                .customerPhone(booking.getCustomerPhone())
                .bookingTime(booking.getBookingTime())
                .partySize(booking.getPartySize())
                .depositAmount(booking.getDepositAmount())
                .status(booking.getStatus())
                .specialRequest(booking.getSpecialRequest())
                .build();
    }
}
