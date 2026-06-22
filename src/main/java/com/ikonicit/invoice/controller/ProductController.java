package com.ikonicit.invoice.controller;

import com.ikonicit.invoice.dto.ProductRequestDTO;
import com.ikonicit.invoice.dto.ProductResponseDTO;
import com.ikonicit.invoice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ─── POST — Create ───────────────────────────────
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(
            @RequestBody ProductRequestDTO requestDTO) {
        return ResponseEntity.ok(
                productService.create(requestDTO));
    }

    // ─── PUT — Update ────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @RequestBody ProductRequestDTO requestDTO) {
        return ResponseEntity.ok(
                productService.update(id, requestDTO));
    }

    // ─── GET ALL ─────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        return ResponseEntity.ok(
                productService.getAll());
    }

    // ─── GET BY ID ───────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                productService.getById(id));
    }

    // ─── DELETE ──────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(
                "Product deleted successfully.");
    }
}