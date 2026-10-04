package com.restaurant.erp.table.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.table.dto.request.CreateAreaRequest;
import com.restaurant.erp.table.dto.request.CreateTableRequest;
import com.restaurant.erp.table.dto.request.UpdateAreaRequest;
import com.restaurant.erp.table.dto.request.UpdateTableRequest;
import com.restaurant.erp.table.dto.response.AreaResponse;
import com.restaurant.erp.table.dto.response.TableResponse;
import com.restaurant.erp.table.entity.DiningTable.TableStatus;
import com.restaurant.erp.table.service.TableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
@Tag(name = "Table & Area Management", description = "APIs for restaurant tables and areas")
public class TableController {

    private final TableService tableService;

    @GetMapping("/areas")
    @Operation(summary = "Lấy danh sách các khu vực bàn ăn")
    public ResponseEntity<ApiResponse<List<AreaResponse>>> getAreas() {
        return ResponseEntity.ok(ApiResponse.success(tableService.getAreaResponses()));
    }

    @PostMapping("/areas")
    @Operation(summary = "Tạo khu vực bàn ăn mới")
    public ResponseEntity<ApiResponse<AreaResponse>> createArea(@Valid @RequestBody CreateAreaRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tableService.createArea(request), "Tạo khu vực thành công"));
    }

    @PutMapping("/areas/{id}")
    @Operation(summary = "Cập nhật tên khu vực bàn ăn")
    public ResponseEntity<ApiResponse<AreaResponse>> updateArea(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateAreaRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tableService.updateArea(id, request), "Cập nhật khu vực thành công"));
    }

    @DeleteMapping("/areas/{id}")
    @Operation(summary = "Xóa khu vực bàn ăn")
    public ResponseEntity<ApiResponse<Void>> deleteArea(@PathVariable Integer id) {
        tableService.deleteArea(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa khu vực thành công"));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách bàn ăn (có thể lọc theo khu vực)")
    public ResponseEntity<ApiResponse<List<TableResponse>>> getTables(@RequestParam(required = false) Integer areaId) {
        return ResponseEntity.ok(ApiResponse.success(tableService.getTableResponses(areaId)));
    }

    @PostMapping
    @Operation(summary = "Tạo bàn ăn mới với validation")
    public ResponseEntity<ApiResponse<TableResponse>> createTable(@Valid @RequestBody CreateTableRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tableService.createTable(request), "Tạo bàn ăn thành công"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin bàn ăn")
    public ResponseEntity<ApiResponse<TableResponse>> updateTable(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateTableRequest request) {
        return ResponseEntity.ok(ApiResponse.success(tableService.updateTable(id, request), "Cập nhật bàn ăn thành công"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa bàn ăn")
    public ResponseEntity<ApiResponse<Void>> deleteTable(@PathVariable Integer id) {
        tableService.deleteTable(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa bàn ăn thành công"));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Cập nhật trạng thái bàn ăn (available, occupied, reserved, cleaning)")
    public ResponseEntity<ApiResponse<TableResponse>> updateTableStatus(
            @PathVariable Integer id,
            @RequestParam TableStatus status) {
        return ResponseEntity.ok(ApiResponse.success(tableService.updateTableStatus(id, status), "Cập nhật trạng thái bàn ăn thành công"));
    }
}