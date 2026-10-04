package com.restaurant.erp.table.service;

import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.repository.BranchRepository;
import com.restaurant.erp.common.context.BranchContext;
import com.restaurant.erp.common.exception.BusinessException;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import com.restaurant.erp.table.dto.TableDto;
import com.restaurant.erp.table.dto.request.CreateAreaRequest;
import com.restaurant.erp.table.dto.request.CreateTableRequest;
import com.restaurant.erp.table.dto.request.UpdateAreaRequest;
import com.restaurant.erp.table.dto.request.UpdateTableRequest;
import com.restaurant.erp.table.dto.response.AreaResponse;
import com.restaurant.erp.table.dto.response.TableResponse;
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

    @Transactional(readOnly = true)
    public List<AreaResponse> getAreaResponses() {
        Branch branch = getBranch();
        List<Area> areas = areaRepository.findByBranchId(branch.getId());
        return areas.stream().map(area -> {
            int count = tableRepository.findByBranchIdAndAreaId(branch.getId(), area.getId()).size();
            return AreaResponse.builder()
                    .id(area.getId())
                    .branchId(branch.getId())
                    .name(area.getName())
                    .tableCount(count)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TableDto.AreaDto> getAreas() {
        Branch branch = getBranch();
        List<Area> areas = areaRepository.findByBranchId(branch.getId());
        return areas.stream().map(this::mapAreaToDto).collect(Collectors.toList());
    }

    @Transactional
    public AreaResponse createArea(CreateAreaRequest request) {
        Branch branch = getBranch();
        String areaName = request.getName() != null ? request.getName().trim() : "";
        if (areaRepository.findByBranchId(branch.getId()).stream()
                .anyMatch(a -> a.getName().equalsIgnoreCase(areaName))) {
            throw new BusinessException("Tên khu vực đã tồn tại trong chi nhánh này");
        }

        Area area = Area.builder()
                .branch(branch)
                .name(areaName)
                .build();
        Area saved = areaRepository.save(area);
        return AreaResponse.builder()
                .id(saved.getId())
                .branchId(branch.getId())
                .name(saved.getName())
                .tableCount(0)
                .build();
    }

    @Transactional
    public TableDto.AreaDto createArea(TableDto.AreaDto dto) {
        CreateAreaRequest req = CreateAreaRequest.builder().name(dto.getName()).build();
        AreaResponse res = createArea(req);
        return TableDto.AreaDto.builder()
                .id(res.getId())
                .branchId(res.getBranchId())
                .name(res.getName())
                .build();
    }

    @Transactional
    public AreaResponse updateArea(Integer id, UpdateAreaRequest request) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Area not found with id: " + id));
        String areaName = request.getName() != null ? request.getName().trim() : "";
        if (areaRepository.findByBranchId(area.getBranch().getId()).stream()
                .anyMatch(a -> !a.getId().equals(id) && a.getName().equalsIgnoreCase(areaName))) {
            throw new BusinessException("Tên khu vực đã tồn tại trong chi nhánh này");
        }

        area.setName(areaName);
        Area saved = areaRepository.save(area);
        int count = tableRepository.findByBranchIdAndAreaId(area.getBranch().getId(), saved.getId()).size();
        return AreaResponse.builder()
                .id(saved.getId())
                .branchId(area.getBranch().getId())
                .name(saved.getName())
                .tableCount(count)
                .build();
    }

    @Transactional
    public TableDto.AreaDto updateArea(Integer id, TableDto.AreaDto dto) {
        UpdateAreaRequest req = UpdateAreaRequest.builder().name(dto.getName()).build();
        AreaResponse res = updateArea(id, req);
        return TableDto.AreaDto.builder()
                .id(res.getId())
                .branchId(res.getBranchId())
                .name(res.getName())
                .build();
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

    @Transactional(readOnly = true)
    public List<TableResponse> getTableResponses(Integer areaId) {
        Branch branch = getBranch();
        List<DiningTable> tables = (areaId != null)
                ? tableRepository.findByBranchIdAndAreaId(branch.getId(), areaId)
                : tableRepository.findByBranchId(branch.getId());
        return tables.stream().map(this::mapToTableResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TableDto> getTables(Integer areaId) {
        Branch branch = getBranch();
        List<DiningTable> tables = (areaId != null)
                ? tableRepository.findByBranchIdAndAreaId(branch.getId(), areaId)
                : tableRepository.findByBranchId(branch.getId());
        return tables.stream().map(this::mapTableToDto).collect(Collectors.toList());
    }

    @Transactional
    public TableResponse createTable(CreateTableRequest request) {
        Branch branch = getBranch();
        Area area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Area not found with id: " + request.getAreaId()));

        String tableNumber = request.getName();

        if (tableRepository.findByBranchIdAndAreaId(branch.getId(), area.getId()).stream()
                .anyMatch(t -> t.getTableNumber().equalsIgnoreCase(tableNumber))) {
            throw new BusinessException("Tên bàn đã tồn tại trong khu vực này");
        }

        DiningTable table = DiningTable.builder()
                .branch(branch)
                .area(area)
                .tableNumber(tableNumber)
                .capacity(request.getCapacity() != null ? request.getCapacity() : 4)
                .status(request.getStatus() != null ? request.getStatus() : DiningTable.TableStatus.AVAILABLE)
                .build();

        return mapToTableResponse(tableRepository.save(table));
    }

    @Transactional
    public TableDto createTable(TableDto dto) {
        CreateTableRequest req = CreateTableRequest.builder()
                .areaId(dto.getAreaId())
                .name(dto.getName())
                .tableNumber(dto.getTableNumber())
                .capacity(dto.getCapacity())
                .status(dto.getStatus())
                .build();
        TableResponse res = createTable(req);
        return mapTableToDto(tableRepository.findById(res.getId()).orElse(null));
    }

    @Transactional
    public TableResponse updateTable(Integer id, UpdateTableRequest request) {
        DiningTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + id));

        String newTableNumber = request.getName();

        if (request.getAreaId() != null && !request.getAreaId().equals(table.getArea().getId())) {
            Area newArea = areaRepository.findById(request.getAreaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Area not found with id: " + request.getAreaId()));
            table.setArea(newArea);
        }

        if (newTableNumber != null && !newTableNumber.trim().isEmpty()) {
            Integer targetAreaId = table.getArea().getId();
            if (tableRepository.findByBranchIdAndAreaId(table.getBranch().getId(), targetAreaId).stream()
                    .anyMatch(t -> !t.getId().equals(id) && t.getTableNumber().equalsIgnoreCase(newTableNumber))) {
                throw new BusinessException("Tên bàn đã tồn tại trong khu vực này");
            }
            table.setTableNumber(newTableNumber);
        }
        if (request.getCapacity() != null && request.getCapacity() > 0) {
            table.setCapacity(request.getCapacity());
        }
        if (request.getStatus() != null) {
            table.setStatus(request.getStatus());
        }

        return mapToTableResponse(tableRepository.save(table));
    }

    @Transactional
    public TableDto updateTable(Integer id, TableDto dto) {
        UpdateTableRequest req = UpdateTableRequest.builder()
                .areaId(dto.getAreaId())
                .name(dto.getName())
                .tableNumber(dto.getTableNumber())
                .capacity(dto.getCapacity())
                .status(dto.getStatus())
                .build();
        TableResponse res = updateTable(id, req);
        return mapTableToDto(tableRepository.findById(res.getId()).orElse(null));
    }

    @Transactional
    public void deleteTable(Integer id) {
        DiningTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + id));
        tableRepository.delete(table);
    }

    @Transactional
    public TableResponse updateTableStatus(Integer tableId, DiningTable.TableStatus status) {
        DiningTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + tableId));
        table.setStatus(status);
        return mapToTableResponse(tableRepository.save(table));
    }

    public TableResponse mapToTableResponse(DiningTable table) {
        if (table == null) return null;
        return TableResponse.builder()
                .id(table.getId())
                .branchId(table.getBranch().getId())
                .areaId(table.getArea().getId())
                .areaName(table.getArea().getName())
                .tableNumber(table.getTableNumber())
                .name(table.getTableNumber())
                .capacity(table.getCapacity())
                .status(table.getStatus())
                .createdAt(table.getCreatedAt())
                .build();
    }

    public TableDto mapTableToDto(DiningTable table) {
        if (table == null) return null;
        return TableDto.builder()
                .id(table.getId())
                .branchId(table.getBranch().getId())
                .areaId(table.getArea().getId())
                .areaName(table.getArea().getName())
                .tableNumber(table.getTableNumber())
                .name(table.getTableNumber())
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