package com.restaurant.erp.branch.controller;

import com.restaurant.erp.branch.dto.BranchDto;
import com.restaurant.erp.branch.service.BranchService;
import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.common.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
@Tag(name = "Branch Management", description = "APIs for managing restaurant branches")
public class BranchController {

    private final BranchService branchService;
    private final CloudinaryService cloudinaryService;

    @GetMapping
    @Operation(summary = "Get all branches")
    public ResponseEntity<ApiResponse<List<BranchDto>>> getAllBranches() {
        return ResponseEntity.ok(ApiResponse.success(branchService.getAllBranches()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get branch details by ID")
    public ResponseEntity<ApiResponse<BranchDto>> getBranchById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(branchService.getBranchById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new branch")
    public ResponseEntity<ApiResponse<BranchDto>> createBranch(@Valid @RequestBody BranchDto dto) {
        return ResponseEntity.ok(ApiResponse.success(branchService.createBranch(dto), "Tạo chi nhánh thành công"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update branch details")
    public ResponseEntity<ApiResponse<BranchDto>> updateBranch(
            @PathVariable Integer id,
            @Valid @RequestBody BranchDto dto) {
        return ResponseEntity.ok(ApiResponse.success(branchService.updateBranch(id, dto), "Cập nhật chi nhánh thành công"));
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and update branch image via Cloudinary")
    public ResponseEntity<ApiResponse<BranchDto>> uploadBranchImage(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = cloudinaryService.uploadImage(file, "branches");
        BranchDto updatedBranch = branchService.updateBranchImage(id, imageUrl);
        return ResponseEntity.ok(ApiResponse.success(updatedBranch, "Tải ảnh chi nhánh lên Cloudinary thành công"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete branch by ID")
    public ResponseEntity<ApiResponse<Void>> deleteBranch(@PathVariable Integer id) {
        branchService.deleteBranch(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa chi nhánh thành công"));
    }
}