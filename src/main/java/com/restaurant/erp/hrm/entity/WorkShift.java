package com.restaurant.erp.hrm.entity;

import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.user.entity.User;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "work_shifts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate shiftDate;

    private ZonedDateTime scheduledStart;

    private ZonedDateTime scheduledEnd;

    private ZonedDateTime actualCheckIn;

    private ZonedDateTime actualCheckOut;

    @Enumerated(EnumType.STRING)
    private ShiftStatus status;

    private String checkInMethod;

    private ZonedDateTime createdAt;

    public enum ShiftStatus {
        SCHEDULED,
        CHECKED_IN,
        CHECKED_OUT,
        ABSENT
    }
}