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
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── Invoice Number ───────────────────────────────────
    // Auto-generated, e.g. INV-2025-001
    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    // ─── Client ───────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // ─── Dates ────────────────────────────────────────────
    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    // Net 15, Net 30, Net 60, Due on Receipt
    @Column(name = "payment_terms")
    private String paymentTerms;

    // Auto-calculated: invoiceDate + paymentTerms days
    @Column(name = "due_date")
    private LocalDate dueDate;

    // ─── References ───────────────────────────────────────
    // Client's internal PO number or reference
    @Column(name = "your_reference")
    private String yourReference;

    // Our internal person/project reference
    @Column(name = "our_reference")
    private String ourReference;

    // ─── Status ───────────────────────────────────────────
    // DRAFT, SENT, PAID, OVERDUE, CANCELLED
    @Column(name = "status", nullable = false)
    private String status = "DRAFT";

    // ─── Amounts ──────────────────────────────────────────
    @Column(name = "subtotal", precision = 18, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 18, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // ─── Currency ─────────────────────────────────────────
    // INR or SEK depending on company
    @Column(name = "currency")
    private String currency = "INR";

    // ─── Notes ────────────────────────────────────────────
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ─── Line Items ───────────────────────────────────────
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @Column(name = "amount_paid", precision = 12, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;   // running total, kept in sync

    @Column(nullable = false)
    private boolean paid = false;

    // ─── Soft Delete ──────────────────────────────────────
    @Column(name = "is_active")
    private Boolean isActive = true;

    // ─── Timestamps ───────────────────────────────────────
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}