package com.restaurant.erp.branch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.erp.branch.dto.BranchCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class BranchControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateBranchValidationFailureAllFields() throws Exception {
        BranchCreateRequest invalidRequest = BranchCreateRequest.builder()
                .code("") // blank -> invalid
                .name("") // blank -> invalid
                .address("12") // < 5 chars -> invalid
                .phone("12345") // not 10 digits starting with 0 -> invalid
                .email("invalid-email") // invalid email format -> invalid
                .taxCode("") // blank -> invalid
                .managerName("") // blank -> invalid
                .openingTime(LocalTime.of(23, 0)) // opening > closing -> invalid
                .closingTime(LocalTime.of(8, 0))
                .totalTables(0) // < 1 -> invalid
                .build();

        mockMvc.perform(post("/api/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data.code").exists())
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.address").exists())
                .andExpect(jsonPath("$.data.phone").exists())
                .andExpect(jsonPath("$.data.email").exists())
                .andExpect(jsonPath("$.data.taxCode").exists())
                .andExpect(jsonPath("$.data.managerName").exists())
                .andExpect(jsonPath("$.data.closingTime").exists())
                .andExpect(jsonPath("$.data.totalTables").exists());
    }
}
