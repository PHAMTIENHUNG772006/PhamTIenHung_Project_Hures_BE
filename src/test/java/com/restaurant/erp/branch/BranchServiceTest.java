package com.restaurant.erp.branch;

import com.restaurant.erp.branch.dto.BranchDto;
import com.restaurant.erp.branch.entity.emuns.BranchStatus;
import com.restaurant.erp.branch.service.BranchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
public class BranchServiceTest {

    @Autowired
    private BranchService branchService;

    @Test
    void testCreateBranchWithMaintenance() {
        BranchDto dto = BranchDto.builder()
                .name("Chi nhánh Test Yên Nghĩa")
                .address("Số 68 Yên Nghĩa")
                .phone("08943434")
                .email("hungpham2@gmail.com")
                .openingTime(LocalTime.of(8, 0))
                .closingTime(LocalTime.of(22, 30))
                .status(BranchStatus.MAINTENANCE)
                .build();

        BranchDto created = branchService.createBranch(dto);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Chi nhánh Test Yên Nghĩa", created.getName());
        assertEquals(BranchStatus.MAINTENANCE, created.getStatus());
        System.out.println(">>> CREATED BRANCH: " + created.getId() + " - " + created.getCode() + " - " + created.getStatus());
    }
}
