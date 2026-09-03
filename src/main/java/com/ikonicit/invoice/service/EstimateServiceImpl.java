package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.*;
import com.ikonicit.invoice.entity.*;
import com.ikonicit.invoice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstimateServiceImpl implements EstimateService {

    private final EstimateRepository estimateRepo;
    private final ClientRepository clientRepo;
    private final ProductRepository productRepo;

    // ─── CREATE ──────────────────────────────────────
    @Override
    @Transactional
    public EstimateResponseDTO create(EstimateRequestDTO req) {

        // Step 1 — Find Client
        Client client = clientRepo.findById(req.getClientId())
                .orElseThrow(() -> new RuntimeException(
                        "Client not found with id: " + req.getClientId()));

        // Step 2 — Generate Estimate Number
        // e.g. EST-001, EST-002
        Long count = estimateRepo.countAllEstimates();
        String estimateNumber = "EST-" +
                String.format("%03d", count + 1);

        // Step 3 — Build Estimate
        Estimate estimate = new Estimate();
        estimate.setEstimateNumber(estimateNumber);
        estimate.setClient(client);
        estimate.setStatus("DRAFT");
        estimate.setIssueDate(req.getIssueDate() != null
                ? req.getIssueDate() : LocalDate.now());
        estimate.setValidUntil(req.getValidUntil());
        estimate.setNotes(req.getNotes());
        estimate.setActive(true);

        // Step 4 — Build Line Items
        List<EstimateItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalVat = BigDecimal.ZERO;

        for (EstimateItemRequestDTO itemReq : req.getItems()) {

            EstimateItem item = new EstimateItem();
            item.setEstimate(estimate);
            item.setDescription(itemReq.getDescription());
            item.setQuantity(itemReq.getQuantity());
            item.setUnit(itemReq.getUnit());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setTaxPercent(itemReq.getTaxPercent() != null
                    ? itemReq.getTaxPercent() : new BigDecimal("25.00"));

            // If product selected from list
            if (itemReq.getProductId() != null) {
                item.setProductId(itemReq.getProductId());
            }

            // Calculate lineTotal = quantity * unitPrice
            BigDecimal lineTotal = itemReq.getQuantity()
                    .multiply(itemReq.getUnitPrice())
                    .setScale(2, RoundingMode.HALF_UP);
            item.setLineTotal(lineTotal);

            // Calculate taxAmount = lineTotal * taxPercent / 100
            BigDecimal taxAmount = lineTotal
                    .multiply(item.getTaxPercent())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            item.setTaxAmount(taxAmount);

            // Add to totals
            subtotal = subtotal.add(lineTotal);
            totalVat = totalVat.add(taxAmount);

            items.add(item);
        }

        // Step 5 — Set Totals
        estimate.setItems(items);
        estimate.setSubtotal(subtotal);
        estimate.setVatAmount(totalVat);
        estimate.setTotal(subtotal.add(totalVat));

        // Step 6 — Save and Return
        Estimate saved = estimateRepo.save(estimate);
        return toResponse(saved);
    }

    // ─── GET BY ID ───────────────────────────────────
    @Override
    public EstimateResponseDTO getById(Long id) {
        Estimate estimate = estimateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Estimate not found with id: " + id));
        return toResponse(estimate);
    }

    // ─── GET ALL ─────────────────────────────────────
    @Override
    public List<EstimateResponseDTO> getAll() {
        return estimateRepo.findAllActiveEstimates()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─── Entity → Response DTO ───────────────────────
    private EstimateResponseDTO toResponse(Estimate e) {

        // Map items
        List<EstimateItemResponseDTO> itemDTOs = e.getItems() != null
                ? e.getItems().stream().map(item ->
                EstimateItemResponseDTO.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .description(item.getDescription())
                        .quantity(item.getQuantity())
                        .unit(item.getUnit())
                        .unitPrice(item.getUnitPrice())
                        .taxPercent(item.getTaxPercent())
                        .taxAmount(item.getTaxAmount())
                        .lineTotal(item.getLineTotal())
                        .build()
        ).collect(Collectors.toList())
                : new ArrayList<>();

        return EstimateResponseDTO.builder()
                .id(e.getId())
                .estimateNumber(e.getEstimateNumber())
                .clientId(e.getClient().getId())
                .clientName(e.getClient().getCompany() != null
                        ? e.getClient().getCompany()
                        : e.getClient().getFirstName() + " "
                        + e.getClient().getLastName())
                .status(e.getStatus())
                .issueDate(e.getIssueDate())
                .validUntil(e.getValidUntil())
                .subtotal(e.getSubtotal())
                .vatAmount(e.getVatAmount())
                .total(e.getTotal())
                .notes(e.getNotes())
                .sentAt(e.getSentAt())
                .approvedAt(e.getApprovedAt())
                .rejectedAt(e.getRejectedAt())
                .convertedInvoiceId(e.getConvertedInvoiceId())
                .isActive(e.isActive())
                .items(itemDTOs)
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    // ─── UPDATE ──────────────────────────────────────
    @Override
    @Transactional
    public EstimateResponseDTO update(Long id, EstimateRequestDTO req) {

        // Step 1 — Find existing estimate
        Estimate estimate = estimateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Estimate not found with id: " + id));

        // Step 2 — Check status
        // Cannot update if already APPROVED or CONVERTED
        if ("APPROVED".equals(estimate.getStatus())
                || "CONVERTED".equals(estimate.getStatus())) {
            throw new RuntimeException(
                    "Cannot update estimate with status: "
                            + estimate.getStatus());
        }

        // Step 3 — Update Client
        if (req.getClientId() != null) {
            Client client = clientRepo.findById(req.getClientId())
                    .orElseThrow(() -> new RuntimeException(
                            "Client not found with id: " + req.getClientId()));
            estimate.setClient(client);
        }

        // Step 4 — Update basic fields
        if (req.getIssueDate() != null)
            estimate.setIssueDate(req.getIssueDate());

        if (req.getValidUntil() != null)
            estimate.setValidUntil(req.getValidUntil());

        if (req.getNotes() != null)
            estimate.setNotes(req.getNotes());

        // Step 5 — Update Line Items
        // Clear old items and add new ones
        estimate.getItems().clear();

        List<EstimateItem> newItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalVat = BigDecimal.ZERO;

        for (EstimateItemRequestDTO itemReq : req.getItems()) {

            EstimateItem item = new EstimateItem();
            item.setEstimate(estimate);
            item.setDescription(itemReq.getDescription());
            item.setQuantity(itemReq.getQuantity());
            item.setUnit(itemReq.getUnit());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setTaxPercent(itemReq.getTaxPercent() != null
                    ? itemReq.getTaxPercent() : new BigDecimal("25.00"));

            if (itemReq.getProductId() != null)
                item.setProductId(itemReq.getProductId());

            // Calculate lineTotal
            BigDecimal lineTotal = itemReq.getQuantity()
                    .multiply(itemReq.getUnitPrice())
                    .setScale(2, RoundingMode.HALF_UP);
            item.setLineTotal(lineTotal);

            // Calculate taxAmount
            BigDecimal taxAmount = lineTotal
                    .multiply(item.getTaxPercent())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            item.setTaxAmount(taxAmount);

            subtotal = subtotal.add(lineTotal);
            totalVat = totalVat.add(taxAmount);

            newItems.add(item);
        }

        // Step 6 — Update totals
        estimate.getItems().addAll(newItems);
        estimate.setSubtotal(subtotal);
        estimate.setVatAmount(totalVat);
        estimate.setTotal(subtotal.add(totalVat));

        // Step 7 — Save and return
        return toResponse(estimateRepo.save(estimate));
    }

    // ─── DELETE (Soft Delete) ────────────────────────
    @Override
    @Transactional
    public void delete(Long id) {

        Estimate estimate = estimateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Estimate not found with id: " + id));

        // Cannot delete if APPROVED or CONVERTED
        if ("APPROVED".equals(estimate.getStatus())
                || "CONVERTED".equals(estimate.getStatus())) {
            throw new RuntimeException(
                    "Cannot delete estimate with status: "
                            + estimate.getStatus());
        }

        estimate.setActive(false);
        estimateRepo.save(estimate);
    }
}