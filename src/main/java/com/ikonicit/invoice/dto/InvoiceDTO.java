package com.ikonicit.invoice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class InvoiceDTO {

    private Long id;

    private String invoiceNumber;

    // Only send client id from frontend
    private Long clientId;

    // For displaying client name in list/view
    private String clientName;

    private LocalDate invoiceDate;

    private String paymentTerms;       // "Net 30", "Net 15" etc.

    private LocalDate dueDate;         // Auto calculated, but also editable

    private String yourReference;      // Client's PO number

    private String ourReference;       // Our internal person/project

    private String status;             // DRAFT, SENT, PAID, OVERDUE, CANCELLED

    private BigDecimal subtotal;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;

    private String currency;           // INR or SEK

    private String notes;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // All product rows of this invoice
    private List<InvoiceItemDTO> items = new ArrayList<>();
}