package com.restaurant.erp.branch.service;

import com.restaurant.erp.branch.dto.BranchDto;
import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.repository.BranchRepository;
import com.restaurant.erp.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public List<BranchDto> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BranchDto getBranchById(Integer id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));
        return mapToDto(branch);
    }

    @Transactional
    public BranchDto createBranch(BranchDto dto) {
        Branch branch = Branch.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
        return mapToDto(branchRepository.save(branch));
    }

    @Transactional
    public BranchDto updateBranch(Integer id, BranchDto dto) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));
        branch.setName(dto.getName());
        branch.setAddress(dto.getAddress());
        branch.setPhone(dto.getPhone());
        if (dto.getIsActive() != null) {
            branch.setIsActive(dto.getIsActive());
        }
        return mapToDto(branchRepository.save(branch));
    }

    public Branch mapToEntity(BranchDto dto) {
        if (dto == null) return null;
        return Branch.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .isActive(dto.getIsActive())
                .build();
    }

    public BranchDto mapToDto(Branch branch) {
        if (branch == null) return null;
        return BranchDto.builder()
                .id(branch.getId())
                .code(branch.getCode())
                .name(branch.getName())
                .address(branch.getAddress())
                .phone(branch.getPhone())
                .isActive(branch.getIsActive())
                .build();
    }
}
