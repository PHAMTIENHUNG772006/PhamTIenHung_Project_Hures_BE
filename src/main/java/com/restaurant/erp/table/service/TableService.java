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

import java.util.ArrayList;
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
            // Default to first branch if context is not explicitly provided
            return branchRepository.findAll().stream().findFirst().map(Branch::getId).orElse(1);
        }
        return branchId;
    }

    private Branch getBranch() {
        Integer branchId = getActiveBranchId();
        return branchRepository.findById(branchId)
                .orElseGet(() -> branchRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No branches available in system")));
    }

    @Transactional
    public List<TableDto.AreaDto> getAreas() {
        Branch branch = getBranch();
        List<Area> areas = areaRepository.findByBranchId(branch.getId());
        if (areas.isEmpty()) {
            // Seed default areas if branch has none
            Area a1 = areaRepository.save(Area.builder().branch(branch).name("Khu A").build());
            Area a2 = areaRepository.save(Area.builder().branch(branch).name("Khu VIP").build());
            Area a3 = areaRepository.save(Area.builder().branch(branch).name("Khu Terrace").build());

            // Seed initial tables with capacities
            tableRepository.save(DiningTable.builder().branch(branch).area(a1).tableNumber("Bàn A1").capacity(4).status(DiningTable.TableStatus.AVAILABLE).build());
            tableRepository.save(DiningTable.builder().branch(branch).area(a1).tableNumber("Bàn A2").capacity(4).status(DiningTable.TableStatus.OCCUPIED).build());
            tableRepository.save(DiningTable.builder().branch(branch).area(a1).tableNumber("Bàn A3").capacity(6).status(DiningTable.TableStatus.AVAILABLE).build());
            tableRepository.save(DiningTable.builder().branch(branch).area(a1).tableNumber("Bàn A4").capacity(2).status(DiningTable.TableStatus.AVAILABLE).build());

            tableRepository.save(DiningTable.builder().branch(branch).area(a2).tableNumber("Bàn VIP 1").capacity(8).status(DiningTable.TableStatus.AVAILABLE).build());
            tableRepository.save(DiningTable.builder().branch(branch).area(a2).tableNumber("Bàn VIP 2").capacity(10).status(DiningTable.TableStatus.OCCUPIED).build());

            tableRepository.save(DiningTable.builder().branch(branch).area(a3).tableNumber("Bàn Terrace 1").capacity(4).status(DiningTable.TableStatus.AVAILABLE).build());
            tableRepository.save(DiningTable.builder().branch(branch).area(a3).tableNumber("Bàn Terrace 2").capacity(4).status(DiningTable.TableStatus.RESERVED).build());

            areas = areaRepository.findByBranchId(branch.getId());
        }
        return areas.stream().map(this::mapAreaToDto).collect(Collectors.toList());
    }

    @Transactional
    public TableDto.AreaDto createArea(TableDto.AreaDto dto) {
        Branch branch = getBranch();
        Area area = Area.builder()
                .branch(branch)
                .name(dto.getName())
                .build();
        return mapAreaToDto(areaRepository.save(area));
    }

    @Transactional
    public TableDto.AreaDto updateArea(Integer id, TableDto.AreaDto dto) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Area not found with id: " + id));
        area.setName(dto.getName());
        return mapAreaToDto(areaRepository.save(area));
    }

    @Transactional
    public void deleteArea(Integer id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Area not found with id: " + id));
        // Delete child tables in this area
        List<DiningTable> tables = tableRepository.findByBranchIdAndAreaId(area.getBranch().getId(), id);
        tableRepository.deleteAll(tables);
        areaRepository.delete(area);
    }

    public List<TableDto> getTables(Integer areaId) {
        Branch branch = getBranch();
        // Ensure areas/tables are initialized
        getAreas();

        List<DiningTable> tables = (areaId != null)
                ? tableRepository.findByBranchIdAndAreaId(branch.getId(), areaId)
                : tableRepository.findByBranchId(branch.getId());
        return tables.stream().map(this::mapTableToDto).collect(Collectors.toList());
    }

    @Transactional
    public TableDto createTable(TableDto dto) {
        Branch branch = getBranch();
        Area area = areaRepository.findById(dto.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Area not found with id: " + dto.getAreaId()));

        DiningTable table = DiningTable.builder()
                .branch(branch)
                .area(area)
                .tableNumber(dto.getTableNumber())
                .capacity(dto.getCapacity() != null ? dto.getCapacity() : 4)
                .status(dto.getStatus() != null ? dto.getStatus() : DiningTable.TableStatus.AVAILABLE)
                .build();

        return mapTableToDto(tableRepository.save(table));
    }

    @Transactional
    public TableDto updateTable(Integer id, TableDto dto) {
        DiningTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + id));

        if (dto.getTableNumber() != null && !dto.getTableNumber().trim().isEmpty()) {
            table.setTableNumber(dto.getTableNumber().trim());
        }
        if (dto.getCapacity() != null && dto.getCapacity() > 0) {
            table.setCapacity(dto.getCapacity());
        }
        if (dto.getAreaId() != null && !dto.getAreaId().equals(table.getArea().getId())) {
            Area newArea = areaRepository.findById(dto.getAreaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Area not found with id: " + dto.getAreaId()));
            table.setArea(newArea);
        }
        if (dto.getStatus() != null) {
            table.setStatus(dto.getStatus());
        }

        return mapTableToDto(tableRepository.save(table));
    }

    @Transactional
    public void deleteTable(Integer id) {
        DiningTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + id));
        tableRepository.delete(table);
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