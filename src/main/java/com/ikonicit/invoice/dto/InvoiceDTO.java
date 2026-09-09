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
    private Long clientId;
    private String clientName;
    private LocalDate invoiceDate;
    private String paymentTerms;
    private LocalDate dueDate;
    private String yourReference;
    private String ourReference;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String currency;
    private String notes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // All product rows of this invoice
    private List<InvoiceItemDTO> items = new ArrayList<>();

    private List<InvoiceExtraFieldDTO> extraFields;
    private Boolean taxDeductionApplied;
    private Integer taxDeductionPercent;
}