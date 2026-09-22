package com.restaurant.erp.common.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.restaurant.erp.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "svg");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    /**
     * Upload image to Cloudinary in a specific folder
     * @param file MultipartFile to upload
     * @param folder Folder name in Cloudinary (e.g., "branches", "menu_items", "avatars")
     * @return secure image URL
     */
    public String uploadImage(MultipartFile file, String folder) {
        validateFile(file);

        try {
            String folderPath = (folder != null && !folder.trim().isEmpty()) ? "restaurant_erp/" + folder.trim() : "restaurant_erp/uploads";
            
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folderPath,
                    "resource_type", "auto",
                    "use_filename", true,
                    "unique_filename", true
            ));

            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("Uploaded file successfully to Cloudinary: {}", secureUrl);
            return secureUrl;
        } catch (IOException e) {
            log.error("Failed to upload file to Cloudinary", e);
            throw new BusinessException("Lỗi khi tải ảnh lên máy chủ Cloudinary: " + e.getMessage());
        }
    }

    /**
     * Upload image and return full metadata
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> uploadImageWithDetails(MultipartFile file, String folder) {
        validateFile(file);

        try {
            String folderPath = (folder != null && !folder.trim().isEmpty()) ? "restaurant_erp/" + folder.trim() : "restaurant_erp/uploads";

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folderPath,
                    "resource_type", "auto",
                    "use_filename", true,
                    "unique_filename", true
            ));

            return (Map<String, Object>) uploadResult;
        } catch (IOException e) {
            log.error("Failed to upload file to Cloudinary with details", e);
            throw new BusinessException("Lỗi khi tải ảnh lên Cloudinary: " + e.getMessage());
        }
    }

    /**
     * Delete image from Cloudinary by public ID
     */
    public boolean deleteImage(String publicId) {
        if (publicId == null || publicId.trim().isEmpty()) {
            return false;
        }

        try {
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String resultStr = (String) result.get("result");
            log.info("Deleted Cloudinary image {}: {}", publicId, resultStr);
            return "ok".equalsIgnoreCase(resultStr);
        } catch (IOException e) {
            log.error("Failed to delete Cloudinary image: {}", publicId, e);
            return false;
        }
    }

    /**
     * Extract public_id from Cloudinary URL if replacing an existing image
     */
    public String extractPublicId(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
            return null;
        }
        try {
            // URL pattern: https://res.cloudinary.com/.../upload/v123456789/restaurant_erp/branches/sample.jpg
            int uploadIndex = imageUrl.indexOf("/upload/");
            if (uploadIndex == -1) return null;
            
            String pathAfterUpload = imageUrl.substring(uploadIndex + 8);
            // Skip version if present (v12345678/)
            if (pathAfterUpload.startsWith("v")) {
                int slashIndex = pathAfterUpload.indexOf('/');
                if (slashIndex != -1) {
                    pathAfterUpload = pathAfterUpload.substring(slashIndex + 1);
                }
            }
            // Remove file extension
            int lastDot = pathAfterUpload.lastIndexOf('.');
            if (lastDot != -1) {
                pathAfterUpload = pathAfterUpload.substring(0, lastDot);
            }
            return pathAfterUpload;
        } catch (Exception e) {
            log.debug("Could not parse publicId from url: {}", imageUrl);
            return null;
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Tệp tải lên không được rỗng");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("Kích thước tệp vượt quá giới hạn tối đa (10MB)");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException("Tệp tải lên không hợp lệ");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("Định dạng tệp không được hỗ trợ. Vui lòng chọn ảnh (jpg, png, webp, svg)");
        }
    }
}
