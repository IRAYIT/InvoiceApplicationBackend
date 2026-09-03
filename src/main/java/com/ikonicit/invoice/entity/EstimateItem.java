package com.ikonicit.invoice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "estimate_item")
public class EstimateItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Parent Estimate
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimate_id", nullable = false)
    private Estimate estimate;

    // Product reference (optional)
    @Column(name = "product_id")
    private Long productId;           // ← must be productId

    // Line Item Fields
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "quantity", precision = 18, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit")
    private String unit;

    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice;     // ← must be unitPrice

    @Column(name = "tax_percent", precision = 5, scale = 2)
    private BigDecimal taxPercent;    // ← must be taxPercent

    @Column(name = "tax_amount", precision = 18, scale = 2)
    private BigDecimal taxAmount;     // ← must be taxAmount

    @Column(name = "line_total", precision = 18, scale = 2)
    private BigDecimal lineTotal;
}