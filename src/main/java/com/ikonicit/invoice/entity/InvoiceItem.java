package com.ikonicit.invoice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "invoice_item")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── Parent Invoice ───────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    // ─── Product reference (optional) ────────────────────
    // If picked from product register, store the id
    // Nullable because user can also type manually
    @Column(name = "product_id")
    private Long productId;

    // ─── Line Item Fields ─────────────────────────────────
    @Column(name = "description", nullable = false)
    private String description;         // Product name or custom text

    @Column(name = "extra_info", columnDefinition = "TEXT")
    private String extraInfo;

    @Column(name = "quantity", precision = 18, scale = 2)
    private BigDecimal quantity;        // e.g. 40 (hours)

    @Column(name = "unit")
    private String unit;                // e.g. "hrs", "pcs", "days"

    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice;       // Price per unit

    @Column(name = "tax_percent", precision = 5, scale = 2)
    private BigDecimal taxPercent;      // e.g. 18.00 for GST 18%

    // ─── NEW: Discount ────────────────────────────────────
    @Column(name = "discount_percent", precision = 5, scale = 2)
    private BigDecimal discountPercent; // e.g. 10.00 for 10% off

    @Column(name = "tax_amount", precision = 18, scale = 2)
    private BigDecimal taxAmount;       // Calculated: lineTotal * taxPercent / 100

    @Column(name = "line_total", precision = 18, scale = 2)
    private BigDecimal lineTotal;       // (quantity * unitPrice) - discount, before tax
}