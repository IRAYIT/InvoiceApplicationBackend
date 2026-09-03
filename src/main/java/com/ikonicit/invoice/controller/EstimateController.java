package com.ikonicit.invoice.controller;

import com.ikonicit.invoice.dto.EstimateRequestDTO;
import com.ikonicit.invoice.dto.EstimateResponseDTO;
import com.ikonicit.invoice.service.EstimateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/estimates")
@RequiredArgsConstructor
public class EstimateController {

    private final EstimateService estimateService;

    // ─── POST — Create ───────────────────────────────
    @PostMapping
    public ResponseEntity<EstimateResponseDTO> create(
            @RequestBody EstimateRequestDTO requestDTO) {
        return ResponseEntity.ok(
                estimateService.create(requestDTO));
    }

    // ─── GET ALL ─────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<EstimateResponseDTO>> getAll() {
        return ResponseEntity.ok(
                estimateService.getAll());
    }

    // ─── GET BY ID ───────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<EstimateResponseDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                estimateService.getById(id));
    }

    // ─── PUT — Update ────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<EstimateResponseDTO> update(
            @PathVariable Long id,
            @RequestBody EstimateRequestDTO requestDTO) {
        return ResponseEntity.ok(
                estimateService.update(id, requestDTO));
    }

    // ─── DELETE ──────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable Long id) {
        estimateService.delete(id);
        return ResponseEntity.ok(
                "Estimate deleted successfully.");
    }


}