package com.restaurant.erp.common.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.common.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Tag(name = "Cloudinary Upload", description = "APIs for uploading images to Cloudinary")
public class UploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload general image to Cloudinary")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        String url = cloudinaryService.uploadImage(file, folder);
        return ResponseEntity.ok(ApiResponse.success(Map.of("url", url), "Tải ảnh lên thành công"));
    }

    @PostMapping(value = "/branch-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload branch image to Cloudinary")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadBranchImage(
            @RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.uploadImage(file, "branches");
        return ResponseEntity.ok(ApiResponse.success(Map.of("url", url), "Tải ảnh chi nhánh lên thành công"));
    }

    @PostMapping(value = "/menu-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload menu item image to Cloudinary")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadMenuImage(
            @RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.uploadImage(file, "menu_items");
        return ResponseEntity.ok(ApiResponse.success(Map.of("url", url), "Tải ảnh món ăn lên thành công"));
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload user avatar to Cloudinary")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.uploadImage(file, "avatars");
        return ResponseEntity.ok(ApiResponse.success(Map.of("url", url), "Tải ảnh đại diện lên thành công"));
    }

    @DeleteMapping
    @Operation(summary = "Delete image from Cloudinary by public ID or URL")
    public ResponseEntity<ApiResponse<Boolean>> deleteImage(
            @RequestParam(value = "publicId", required = false) String publicId,
            @RequestParam(value = "url", required = false) String url) {
        String targetPublicId = publicId;
        if ((targetPublicId == null || targetPublicId.trim().isEmpty()) && url != null) {
            targetPublicId = cloudinaryService.extractPublicId(url);
        }

        boolean deleted = cloudinaryService.deleteImage(targetPublicId);
        return ResponseEntity.ok(ApiResponse.success(deleted, deleted ? "Xóa ảnh thành công" : "Không thể xóa ảnh"));
    }
}
