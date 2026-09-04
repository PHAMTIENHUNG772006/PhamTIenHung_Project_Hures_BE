package com.restaurant.erp.branch.controller;

import com.restaurant.erp.branch.dto.BranchDto;
import com.restaurant.erp.branch.service.BranchService;
import com.restaurant.erp.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BranchDto>>> getAllBranches() {
        return ResponseEntity.ok(ApiResponse.success(branchService.getAllBranches()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchDto>> getBranchById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(branchService.getBranchById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BranchDto>> createBranch(@Valid @RequestBody BranchDto dto) {
        return ResponseEntity.ok(ApiResponse.success(branchService.createBranch(dto), "Branch created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchDto>> updateBranch(
            @PathVariable Integer id,
            @Valid @RequestBody BranchDto dto) {
        return ResponseEntity.ok(ApiResponse.success(branchService.updateBranch(id, dto), "Branch updated successfully"));
    }
}
