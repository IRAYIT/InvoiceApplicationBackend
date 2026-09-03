package com.ikonicit.invoice.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceItemDTO {

    private Long id;

    private Long invoiceId;

    // If picked from product register
    private Long productId;

    private String description; // Product name or custom text

    private String extraInfo;

    private BigDecimal quantity;       // e.g. 40

    private String unit;               // "hrs", "pcs", "days"

    private BigDecimal unitPrice;      // Price per unit

    private BigDecimal taxPercent;     // e.g. 18.00

    private BigDecimal discountPercent; // e.g. 10.00 for 10% off

    private BigDecimal taxAmount;      // Calculated

    private BigDecimal lineTotal; // (quantity * unitPrice) - discount

    private String rowType;
}