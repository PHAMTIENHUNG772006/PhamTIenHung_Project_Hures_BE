package com.restaurant.erp.inventory.entity;

import com.restaurant.erp.branch.entity.Branch;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(
    name = "daily_ingredient_allocations",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_daily_alloc", columnNames = {"branch_id", "ingredient_id", "allocation_date"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyIngredientAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "allocation_date", nullable = false)
    private LocalDate allocationDate;

    @Column(name = "allocated_quantity", nullable = false)
    private Double allocatedQuantity;

    @Column(name = "used_quantity", nullable = false)
    @Builder.Default
    private Double usedQuantity = 0.0;

    @Column(name = "actual_remaining")
    private Double actualRemaining;

    @Column(name = "returned_quantity")
    @Builder.Default
    private Double returnedQuantity = 0.0;

    @Column(name = "loss_quantity")
    @Builder.Default
    private Double lossQuantity = 0.0;

    @Column(name = "loss_reason")
    private String lossReason;

    @Column(name = "source", nullable = false)
    @Builder.Default
    private String source = "LOCAL_STOCK";

    @Column(name = "central_batch_no")
    private String centralBatchNo;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private String status = "OPEN";

    @Column(name = "note")
    private String note;

    @Column(name = "created_at")
    @Builder.Default
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private ZonedDateTime updatedAt = ZonedDateTime.now();
}
