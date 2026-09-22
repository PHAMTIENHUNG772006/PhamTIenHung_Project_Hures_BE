package com.restaurant.erp.table.controller;

import com.restaurant.erp.common.response.ApiResponse;
import com.restaurant.erp.table.dto.TableDto;
import com.restaurant.erp.table.entity.DiningTable.TableStatus;
import com.restaurant.erp.table.service.TableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @GetMapping("/areas")
    public ResponseEntity<ApiResponse<List<TableDto.AreaDto>>> getAreas() {
        return ResponseEntity.ok(ApiResponse.success(tableService.getAreas()));
    }

    @PostMapping("/areas")
    public ResponseEntity<ApiResponse<TableDto.AreaDto>> createArea(@Valid @RequestBody TableDto.AreaDto dto) {
        return ResponseEntity.ok(ApiResponse.success(tableService.createArea(dto), "Area created successfully"));
    }

    @PutMapping("/areas/{id}")
    public ResponseEntity<ApiResponse<TableDto.AreaDto>> updateArea(
            @PathVariable Integer id,
            @Valid @RequestBody TableDto.AreaDto dto) {
        return ResponseEntity.ok(ApiResponse.success(tableService.updateArea(id, dto), "Area updated successfully"));
    }

    @DeleteMapping("/areas/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteArea(@PathVariable Integer id) {
        tableService.deleteArea(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Area deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TableDto>>> getTables(@RequestParam(required = false) Integer areaId) {
        return ResponseEntity.ok(ApiResponse.success(tableService.getTables(areaId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TableDto>> createTable(@Valid @RequestBody TableDto dto) {
        return ResponseEntity.ok(ApiResponse.success(tableService.createTable(dto), "Table created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TableDto>> updateTable(
            @PathVariable Integer id,
            @Valid @RequestBody TableDto dto) {
        return ResponseEntity.ok(ApiResponse.success(tableService.updateTable(id, dto), "Table updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTable(@PathVariable Integer id) {
        tableService.deleteTable(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Table deleted successfully"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TableDto>> updateTableStatus(
            @PathVariable Integer id,
            @RequestParam TableStatus status) {
        return ResponseEntity.ok(ApiResponse.success(tableService.updateTableStatus(id, status), "Table status updated successfully"));
    }
}