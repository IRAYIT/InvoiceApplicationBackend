package com.ikonicit.invoice.controller;

import com.ikonicit.invoice.dto.ProductGroupRequestDTO;
import com.ikonicit.invoice.dto.ProductGroupResponseDTO;
import com.ikonicit.invoice.service.ProductGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/product-groups")
@RequiredArgsConstructor
public class ProductGroupController {

    private final ProductGroupService productGroupService;

    // ─── POST — Create ───────────────────────────────
    @PostMapping
    public ResponseEntity<ProductGroupResponseDTO> create(
            @RequestBody ProductGroupRequestDTO requestDTO) {
        return ResponseEntity.ok(
                productGroupService.create(requestDTO));
    }

    // ─── PUT — Update ────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ProductGroupResponseDTO> update(
            @PathVariable Long id,
            @RequestBody ProductGroupRequestDTO requestDTO) {
        return ResponseEntity.ok(
                productGroupService.update(id, requestDTO));
    }

    // ─── GET ALL ─────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<ProductGroupResponseDTO>> getAll() {
        return ResponseEntity.ok(
                productGroupService.getAll());
    }

    // ─── GET BY ID ───────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ProductGroupResponseDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                productGroupService.getById(id));
    }

    // ─── DELETE ──────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id) {
        productGroupService.delete(id);
        return ResponseEntity.ok(
                "Product group deleted successfully.");
    }
}