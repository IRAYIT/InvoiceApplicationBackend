package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.ProductRequestDTO;
import com.ikonicit.invoice.dto.ProductResponseDTO;
import com.ikonicit.invoice.entity.Product;
import com.ikonicit.invoice.entity.ProductGroup;
import com.ikonicit.invoice.repository.ProductGroupRepository;
import com.ikonicit.invoice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final ProductGroupRepository productGroupRepo;

    // ─── CREATE ──────────────────────────────────────
    @Override
    @Transactional
    public ProductResponseDTO create(ProductRequestDTO req) {

        // Step 1 — Check duplicate product code
        if (req.getProductCode() != null
                && productRepo.existsByProductCode(req.getProductCode())) {
            throw new RuntimeException(
                    "Product code already exists: " + req.getProductCode());
        }

        // Step 2 — Build Product
        Product product = new Product();
        product.setName(req.getName());
        product.setUnit(req.getUnit());
        product.setProductCode(req.getProductCode());
        product.setPrice(req.getPrice());
        product.setTax(req.getTax() != null
                ? req.getTax() : new BigDecimal("25.00"));
        product.setRotRutGreenTech(req.getRotRutGreenTech() != null
                ? req.getRotRutGreenTech() : false);
        product.setIsActive(true);

        // Step 3 — Calculate priceInclTax
        // priceInclTax = price + (price * tax / 100)
        product.setPriceInclTax(
                calculatePriceInclTax(product.getPrice(), product.getTax()));

        // Step 4 — Set Product Group (optional)
        if (req.getProductGroupId() != null) {
            ProductGroup group = productGroupRepo
                    .findById(req.getProductGroupId())
                    .orElseThrow(() -> new RuntimeException(
                            "Product group not found with id: "
                                    + req.getProductGroupId()));
            product.setProductGroup(group);
        }

        // Step 5 — Save and return
        return toResponse(productRepo.save(product));
    }

    // ─── UPDATE ──────────────────────────────────────
    @Override
    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO req) {

        // Step 1 — Find existing product
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Product not found with id: " + id));

        // Step 2 — Update fields
        if (req.getName() != null)
            product.setName(req.getName());

        if (req.getUnit() != null)
            product.setUnit(req.getUnit());

        if (req.getProductCode() != null)
            product.setProductCode(req.getProductCode());

        if (req.getPrice() != null)
            product.setPrice(req.getPrice());

        if (req.getTax() != null)
            product.setTax(req.getTax());

        if (req.getRotRutGreenTech() != null)
            product.setRotRutGreenTech(req.getRotRutGreenTech());

        // Step 3 — Recalculate priceInclTax
        product.setPriceInclTax(
                calculatePriceInclTax(product.getPrice(), product.getTax()));

        // Step 4 — Update Product Group
        if (req.getProductGroupId() != null) {
            ProductGroup group = productGroupRepo
                    .findById(req.getProductGroupId())
                    .orElseThrow(() -> new RuntimeException(
                            "Product group not found with id: "
                                    + req.getProductGroupId()));
            product.setProductGroup(group);
        } else {
            // If null sent → remove group (set to None)
            product.setProductGroup(null);
        }

        // Step 5 — Save and return
        return toResponse(productRepo.save(product));
    }

    // ─── GET BY ID ───────────────────────────────────
    @Override
    public ProductResponseDTO getById(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Product not found with id: " + id));
        return toResponse(product);
    }

    // ─── GET ALL ─────────────────────────────────────
    @Override
    public List<ProductResponseDTO> getAll() {
        return productRepo.findAllActiveProducts()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── DELETE (Soft Delete) ────────────────────────
    @Override
    @Transactional
    public void delete(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Product not found with id: " + id));
        product.setIsActive(false);
        productRepo.save(product);
    }

    // ─── Calculate Price Incl Tax ────────────────────
    private BigDecimal calculatePriceInclTax(
            BigDecimal price, BigDecimal tax) {
        if (price == null) return BigDecimal.ZERO;
        if (tax == null) return price;

        // price + (price * tax / 100)
        BigDecimal taxAmount = price
                .multiply(tax)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        return price.add(taxAmount);
    }

    // ─── Entity → Response DTO ───────────────────────
    private ProductResponseDTO toResponse(Product p) {
        return ProductResponseDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .unit(p.getUnit())
                .productCode(p.getProductCode())
                .productGroupId(p.getProductGroup() != null
                        ? p.getProductGroup().getId() : null)
                .productGroupName(p.getProductGroup() != null
                        ? p.getProductGroup().getName() : null)
                .price(p.getPrice())
                .tax(p.getTax())
                .priceInclTax(p.getPriceInclTax())
                .rotRutGreenTech(p.getRotRutGreenTech())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}