package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.ProductGroupRequestDTO;
import com.ikonicit.invoice.dto.ProductGroupResponseDTO;
import com.ikonicit.invoice.entity.ProductGroup;
import com.ikonicit.invoice.repository.ProductGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductGroupServiceImpl implements ProductGroupService {

    private final ProductGroupRepository productGroupRepo;

    // ─── CREATE ──────────────────────────────────────
    @Override
    @Transactional
    public ProductGroupResponseDTO create(ProductGroupRequestDTO req) {

        // Check duplicate name
        if (productGroupRepo.existsByName(req.getName())) {
            throw new RuntimeException(
                    "Product group already exists: " + req.getName());
        }

        ProductGroup group = new ProductGroup();
        group.setName(req.getName());

        return toResponse(productGroupRepo.save(group));
    }

    // ─── UPDATE ──────────────────────────────────────
    @Override
    @Transactional
    public ProductGroupResponseDTO update(Long id, ProductGroupRequestDTO req) {

        ProductGroup group = productGroupRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Product group not found with id: " + id));

        group.setName(req.getName());

        return toResponse(productGroupRepo.save(group));
    }

    // ─── GET BY ID ───────────────────────────────────
    @Override
    public ProductGroupResponseDTO getById(Long id) {

        ProductGroup group = productGroupRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Product group not found with id: " + id));

        return toResponse(group);
    }

    // ─── GET ALL ─────────────────────────────────────
    @Override
    public List<ProductGroupResponseDTO> getAll() {
        return productGroupRepo.findByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── DELETE (Soft Delete) ────────────────────────
    @Override
    @Transactional
    public void delete(Long id) {

        ProductGroup group = productGroupRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Product group not found with id: " + id));

        group.setIsActive(false);
        productGroupRepo.save(group);
    }

    // ─── Entity → Response DTO ───────────────────────
    private ProductGroupResponseDTO toResponse(ProductGroup group) {
        return ProductGroupResponseDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .isActive(group.getIsActive())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }
}