package com.restaurant.erp.branch;

import com.restaurant.erp.branch.dto.BranchDto;
import com.restaurant.erp.branch.dto.request.BranchCreateRequest;
import com.restaurant.erp.branch.dto.response.BranchResponse;
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
    void testCreateBranchWithDtoRequestAndResponse() {
        BranchCreateRequest request = BranchCreateRequest.builder()
                .code("CN_TEST")
                .name("Chi nhánh Test Cầu Giấy")
                .address("123 Cầu Giấy, Quan Hoa, Hà Nội")
                .phone("0912345678")
                .email("caugiay@restaurant.vn")
                .taxCode("0101234567")
                .openingTime(LocalTime.of(8, 0))
                .closingTime(LocalTime.of(22, 30))
                .status(BranchStatus.ACTIVE)
                .managerName("Trần Văn B")
                .totalTables(30)
                .build();

        BranchResponse response = branchService.createBranch(request);
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Chi nhánh Test Cầu Giấy", response.getName());
        assertEquals("0912345678", response.getPhone());
        assertEquals("caugiay@restaurant.vn", response.getEmail());
        assertEquals(BranchStatus.ACTIVE, response.getStatus());
        assertTrue(response.getIsActive());
        assertEquals(30, response.getTotalTables());
        assertNotNull(response.getCode());
        System.out.println(">>> CREATED BRANCH RESPONSE: " + response.getId() + " - " + response.getCode() + " - " + response.getStatus());
    }

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
