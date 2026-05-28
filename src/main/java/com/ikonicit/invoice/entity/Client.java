package com.ikonicit.invoice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

    // Radio button: "company" or "person"
    @Column(name = "client_type", nullable = false)
    private String clientType;

    // Shown when clientType = "company"
    @Column(name = "company")
    private String company;

    // Shown when clientType = "company"
    @Column(name = "company_reg_no")
    private String companyRegNo;        // "Company reg. no."

    // Shown when clientType = "company"
    @Column(name = "vat_no")
    private String vatNo;               // "VAT no."

    // Shown when clientType = "person"
    @Column(name = "first_name")
    private String firstName;           // "First name"

    // Shown when clientType = "person"
    @Column(name = "last_name")
    private String lastName;            // "Last name"

    // Shown when clientType = "person"
    @Column(name = "personal_id_no")
    private String personalIdNo;        // "Personal id no."

    // Shown for both company and person
    @Column(name = "email")
    private String email;               // "Email"

    // ─── Auto-generated client number ────────────────────
    // Visible in list as "#" column (e.g. 2)
    @Column(name = "number")
    private String number;

    // ─── Soft Delete ─────────────────────────────────────
    @Column(name = "is_active")
    private Boolean isActive = true;

    // ─── Timestamps ──────────────────────────────────────
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ─── Billing Address Tab ─────────────────────────────
    // C/O, Address, Zip code, City, Country
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientAddress address;

    // ─── Delivery Address Tab ────────────────────────────
    // Same fields as billing address
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientDeliveryAddress deliveryAddress;

    // ─── Send Invoices Settings ──────────────────────────
    // "Send invoices by" + "Always attach PDF"
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientSettings settings;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}