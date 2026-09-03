package com.ikonicit.invoice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "estimate")
public class Estimate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Auto-generated estimate number e.g. "EST-001"
    @Column(name = "estimate_number", unique = true, nullable = false)
    private String estimateNumber;

    // Which client this estimate is for
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // ─── Status ──────────────────────────────────────
    // DRAFT → SENT → APPROVED / REJECTED
    @Column(name = "status", nullable = false)
    private String status = "DRAFT";

    // ─── Dates ───────────────────────────────────────
    // Date estimate was created
    @Column(name = "issue_date")
    private LocalDate issueDate;

    // Date estimate expires
    @Column(name = "valid_until")
    private LocalDate validUntil;

    // ─── Pricing Totals ──────────────────────────────
    // Sum of all line items before VAT
    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    // Total VAT amount
    @Column(name = "vat_amount", precision = 10, scale = 2)
    private BigDecimal vatAmount = BigDecimal.ZERO;

    // subtotal + vatAmount
    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    // ─── Extra Info ───────────────────────────────────
    // Custom message/note on estimate
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // When estimate was sent to client
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    // When client approved
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // When client rejected
    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    // If converted to invoice, store invoice id
    @Column(name = "converted_invoice_id")
    private Long convertedInvoiceId;

    // Soft delete
    @Column(name = "is_active")
    private boolean active = true;

    // ─── Timestamps ──────────────────────────────────
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ─── Line Items ───────────────────────────────────
    // All products/services in this estimate
    @OneToMany(mappedBy = "estimate",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<EstimateItem> items = new ArrayList<>();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}