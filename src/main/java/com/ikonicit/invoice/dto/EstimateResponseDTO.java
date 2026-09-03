package com.ikonicit.invoice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class EstimateResponseDTO {

    private Long id;

    // e.g. "EST-001"
    private String estimateNumber;

    // Client info
    private Long clientId;
    private String clientName;

    // DRAFT / SENT / APPROVED / REJECTED / CONVERTED
    private String status;

    // Dates
    private LocalDate issueDate;
    private LocalDate validUntil;

    // Totals
    private BigDecimal subtotal;      // before VAT
    private BigDecimal vatAmount;     // total VAT
    private BigDecimal total;         // subtotal + vatAmount

    // Notes
    private String notes;

    // Status timestamps
    private LocalDateTime sentAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;

    // If converted to invoice
    private Long convertedInvoiceId;

    private boolean isActive;

    // Line items
    private List<EstimateItemResponseDTO> items;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}