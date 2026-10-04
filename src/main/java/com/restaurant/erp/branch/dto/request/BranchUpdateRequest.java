package com.restaurant.erp.branch.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.erp.branch.entity.emuns.BranchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "DTO Request dùng để cập nhật thông tin chi nhánh")
public class BranchUpdateRequest {

    @Schema(description = "Mã chi nhánh", example = "CN01")
    @Size(min = 2, max = 20, message = "Mã chi nhánh phải từ 2 đến 20 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Mã chi nhánh chỉ được chứa chữ cái, số, gạch ngang (-) hoặc gạch dưới (_)")
    private String code;

    @Schema(description = "Tên chi nhánh", example = "Chi nhánh Landmark 81")
    @NotBlank(message = "Tên chi nhánh không được để trống")
    @Size(min = 3, max = 100, message = "Tên chi nhánh phải từ 3 đến 100 ký tự")
    private String name;

    @Schema(description = "Địa chỉ hoạt động", example = "720A Điện Biên Phủ, Phường 22, Bình Thạnh, TP.HCM")
    @NotBlank(message = "Địa chỉ chi nhánh không được để trống")
    @Size(min = 5, max = 255, message = "Địa chỉ phải từ 5 đến 255 ký tự")
    private String address;

    @Schema(description = "Số điện thoại liên hệ", example = "02838129999")
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải gồm đúng 10 chữ số và bắt đầu bằng số 0")
    private String phone;

    @Schema(description = "Email liên hệ", example = "landmark@restaurant.vn")
    @Email(message = "Email không đúng định dạng (VD: chinhanh@domain.com)")
    @Size(max = 100, message = "Email không được quá 100 ký tự")
    private String email;

    @Schema(description = "Mã số thuế", example = "0315891234-001")
    @Pattern(regexp = "^[0-9]{10}(-[0-9]{3})?$", message = "Mã số thuế phải gồm 10 số hoặc 13 số")
    private String taxCode;

    @Schema(description = "Giờ mở cửa", example = "08:00")
    @JsonFormat(pattern = "HH:mm[:ss]")
    private LocalTime openingTime;

    @Schema(description = "Giờ đóng cửa", example = "22:30")
    @JsonFormat(pattern = "HH:mm[:ss]")
    private LocalTime closingTime;

    @Schema(description = "Ảnh đại diện chi nhánh")
    private String image;

    @Schema(description = "Trạng thái chi nhánh", example = "ACTIVE")
    private BranchStatus status;

    @Schema(description = "Tên người quản lý chi nhánh", example = "Nguyễn Văn A")
    @Size(min = 2, max = 100, message = "Tên người quản lý phải từ 2 đến 100 ký tự")
    private String managerName;

    @Schema(description = "Tổng số bàn ăn", example = "25")
    @Min(value = 1, message = "Tổng số bàn ăn phải từ 1 bàn trở lên")
    @Max(value = 500, message = "Tổng số bàn ăn không được vượt quá 500 bàn")
    private Integer totalTables;

    @JsonIgnore
    @AssertTrue(message = "Giờ mở cửa phải trước giờ đóng cửa")
    public boolean isOpeningTimeBeforeClosingTime() {
        if (openingTime == null || closingTime == null) {
            return true;
        }
        return openingTime.isBefore(closingTime);
    }
}
