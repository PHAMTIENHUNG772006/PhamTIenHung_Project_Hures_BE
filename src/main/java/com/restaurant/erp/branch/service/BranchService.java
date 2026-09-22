package com.restaurant.erp.branch.service;

import com.restaurant.erp.branch.dto.BranchDto;
import com.restaurant.erp.branch.entity.Branch;
import com.restaurant.erp.branch.entity.emuns.BranchStatus;
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
        String code = dto.getCode();
        if (code == null || code.trim().isEmpty()) {
            long nextNum = branchRepository.count() + 1;
            String candidate = String.format("CN%02d", nextNum);
            while (branchRepository.findByCode(candidate).isPresent()) {
                nextNum++;
                candidate = String.format("CN%02d", nextNum);
            }
            code = candidate;
        } else {
            code = code.trim().toUpperCase();
            if (branchRepository.findByCode(code).isPresent()) {
                long nextNum = branchRepository.count() + 1;
                String candidate = String.format("%s_%02d", code, nextNum);
                while (branchRepository.findByCode(candidate).isPresent()) {
                    nextNum++;
                    candidate = String.format("%s_%02d", code, nextNum);
                }
                code = candidate;
            }
        }

        BranchStatus status = dto.getStatus();
        if (status == null) {
            status = (dto.getIsActive() != null && !dto.getIsActive()) ? BranchStatus.INACTIVE : BranchStatus.ACTIVE;
        }

        Branch branch = Branch.builder()
                .code(code)
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .taxCode(dto.getTaxCode())
                .openingTime(dto.getOpeningTime())
                .closingTime(dto.getClosingTime())
                .image(dto.getImage())
                .status(status)
                .build();
        return mapToDto(branchRepository.save(branch));
    }

    @Transactional
    public BranchDto updateBranch(Integer id, BranchDto dto) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));

        if (dto.getName() != null) branch.setName(dto.getName());
        if (dto.getAddress() != null) branch.setAddress(dto.getAddress());
        if (dto.getPhone() != null) branch.setPhone(dto.getPhone());
        if (dto.getEmail() != null) branch.setEmail(dto.getEmail());
        if (dto.getTaxCode() != null) branch.setTaxCode(dto.getTaxCode());
        if (dto.getOpeningTime() != null) branch.setOpeningTime(dto.getOpeningTime());
        if (dto.getClosingTime() != null) branch.setClosingTime(dto.getClosingTime());
        if (dto.getImage() != null) branch.setImage(dto.getImage());

        if (dto.getCode() != null && !dto.getCode().trim().isEmpty()) {
            branch.setCode(dto.getCode().trim().toUpperCase());
        }

        if (dto.getStatus() != null) {
            branch.setStatus(dto.getStatus());
        } else if (dto.getIsActive() != null) {
            branch.setStatus(dto.getIsActive() ? BranchStatus.ACTIVE : BranchStatus.INACTIVE);
        }

        return mapToDto(branchRepository.save(branch));
    }

    @Transactional
    public BranchDto updateBranchImage(Integer id, String imageUrl) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));
        branch.setImage(imageUrl);
        return mapToDto(branchRepository.save(branch));
    }

    @Transactional
    public void deleteBranch(Integer id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));
        branchRepository.delete(branch);
    }

    public Branch mapToEntity(BranchDto dto) {
        if (dto == null) return null;
        BranchStatus status = dto.getStatus();
        if (status == null && dto.getIsActive() != null) {
            status = dto.getIsActive() ? BranchStatus.ACTIVE : BranchStatus.INACTIVE;
        }

        return Branch.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .taxCode(dto.getTaxCode())
                .openingTime(dto.getOpeningTime())
                .closingTime(dto.getClosingTime())
                .image(dto.getImage())
                .status(status != null ? status : BranchStatus.ACTIVE)
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
                .email(branch.getEmail())
                .taxCode(branch.getTaxCode())
                .openingTime(branch.getOpeningTime())
                .closingTime(branch.getClosingTime())
                .image(branch.getImage())
                .status(branch.getStatus())
                .isActive(branch.getStatus() == BranchStatus.ACTIVE)
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }
}