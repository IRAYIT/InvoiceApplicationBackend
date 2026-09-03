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

    @Column(name = "client_type", nullable = false)
    private String clientType;

    @Column(name = "company")
    private String company;

    @Column(name = "company_reg_no")
    private String companyRegNo;

    @Column(name = "vat_no")
    private String vatNo;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "personal_id_no")
    private String personalIdNo;

    @Column(name = "email")
    private String email;

    // ─── NEW: Contact information section ────────────────
    @Column(name = "website")
    private String website;

    @Column(name = "phone_mobile")
    private String phoneMobile;

    @Column(name = "phone_home")
    private String phoneHome;

    @Column(name = "fax")
    private String fax;

    @Column(name = "number")
    private String number;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientAddress address;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientDeliveryAddress deliveryAddress;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientSettings settings;

    // ─── NEW: "New invoice settings" section ─────────────
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientInvoiceSettings invoiceSettings;

    // ─── NEW: "Extra field for ROT deduction" (Sweden only) ─
    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClientRotInfo rotInfo;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}