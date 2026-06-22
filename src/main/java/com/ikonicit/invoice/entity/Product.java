package com.ikonicit.invoice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "Name" field
    @Column(name = "name", nullable = false)
    private String name;

    // "Unit" field — e.g. "hr", "st", "kg", "day"
    @Column(name = "unit")
    private String unit;

    // "Product code" field — e.g. "WD-001"
    @Column(name = "product_code")
    private String productCode;

    // "Product group" dropdown — optional
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_group_id")
    private ProductGroup productGroup;

    // "Price" — price excluding VAT
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    // "Tax" — VAT percentage e.g. 25.00
    @Column(name = "tax", precision = 5, scale = 2)
    private BigDecimal tax = new BigDecimal("25.00");

    // "Price incl. tax" — calculated field
    @Column(name = "price_incl_tax", precision = 10, scale = 2)
    private BigDecimal priceInclTax;

    // "ROT / RUT / Green tech" checkbox
    @Column(name = "rot_rut_green_tech")
    private Boolean rotRutGreenTech = false;

    // Soft delete
    @Column(name = "is_active")
    private Boolean isActive = true;

    // Timestamps
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}