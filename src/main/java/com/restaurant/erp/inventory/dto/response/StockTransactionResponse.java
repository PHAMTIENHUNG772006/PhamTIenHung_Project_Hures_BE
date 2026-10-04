package com.restaurant.erp.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Thông tin giao dịch kho nguyên vật liệu")
public class StockTransactionResponse {

    @Schema(description = "ID giao dịch", example = "100")
    private Long id;

    @Schema(description = "ID nguyên liệu", example = "1")
    private Integer ingredientId;

    @Schema(description = "Tên nguyên liệu", example = "Thịt bò thăn")
    private String ingredientName;

    @Schema(description = "Người thực hiện", example = "Nguyễn Thủ Kho")
    private String userName;

    @Schema(description = "Loại giao dịch (IMPORT, EXPORT, ADJUSTMENT, RETURN)", example = "IMPORT")
    private String transactionType;

    @Schema(description = "Số lượng", example = "20.0")
    private double quantity;

    @Schema(description = "Đơn giá nhập", example = "150000.0")
    private double unitPrice;

    @Schema(description = "Mã đơn hàng liên quan (nếu là trừ kho tự động)")
    private Long referenceOrderId;

    @Schema(description = "Ghi chú giao dịch")
    private String note;

    @Schema(description = "Thời gian ghi nhận giao dịch")
    private ZonedDateTime createdAt;
}
