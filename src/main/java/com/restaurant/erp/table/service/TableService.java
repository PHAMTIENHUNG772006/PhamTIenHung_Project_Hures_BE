package com.restaurant.erp.table.service;

import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.repository.BranchRepository;
import com.restaurant.erp.common.context.BranchContext;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.table.dto.TableDto;
import com.restaurant.erp.table.entity.Area;
import com.restaurant.erp.table.entity.DiningTable;
import com.restaurant.erp.table.repository.AreaRepository;
import com.restaurant.erp.table.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final AreaRepository areaRepository;
    private final BranchRepository branchRepository;

    private Integer getActiveBranchId() {
        Integer branchId = BranchContext.getCurrentBranchId();
        if (branchId == null) {
            throw new RuntimeException("Branch context is not set");
        }
        return branchId;
    }

    public List<TableDto.AreaDto> getAreas() {
        return areaRepository.findByBranchId(getActiveBranchId()).stream()
                .map(this::mapAreaToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TableDto.AreaDto createArea(TableDto.AreaDto dto) {
        Branch branch = branchRepository.findById(getActiveBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        Area area = Area.builder()
                .branch(branch)
                .name(dto.getName())
                .build();
        return mapAreaToDto(areaRepository.save(area));
    }

    public List<TableDto> getTables(Integer areaId) {
        Integer branchId = getActiveBranchId();
        List<DiningTable> tables = (areaId != null)
                ? tableRepository.findByBranchIdAndAreaId(branchId, areaId)
                : tableRepository.findByBranchId(branchId);
        return tables.stream().map(this::mapTableToDto).collect(Collectors.toList());
    }

    @Transactional
    public TableDto createTable(TableDto dto) {
        Branch branch = branchRepository.findById(getActiveBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        Area area = areaRepository.findById(dto.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Area not found with id: " + dto.getAreaId()));

        DiningTable table = DiningTable.builder()
                .branch(branch)
                .area(area)
                .tableNumber(dto.getTableNumber())
                .capacity(dto.getCapacity())
                .status(dto.getStatus() != null ? dto.getStatus() : DiningTable.TableStatus.AVAILABLE)
                .build();

        return mapTableToDto(tableRepository.save(table));
    }

    @Transactional
    public TableDto updateTableStatus(Integer tableId, DiningTable.TableStatus status) {
        DiningTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + tableId));
        table.setStatus(status);
        return mapTableToDto(tableRepository.save(table));
    }

    public TableDto mapTableToDto(DiningTable table) {
        if (table == null) return null;
        return TableDto.builder()
                .id(table.getId())
                .branchId(table.getBranch().getId())
                .areaId(table.getArea().getId())
                .areaName(table.getArea().getName())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .status(table.getStatus())
                .build();
    }

    public TableDto.AreaDto mapAreaToDto(Area area) {
        if (area == null) return null;
        return TableDto.AreaDto.builder()
                .id(area.getId())
                .branchId(area.getBranch().getId())
                .name(area.getName())
                .build();
    }
}
