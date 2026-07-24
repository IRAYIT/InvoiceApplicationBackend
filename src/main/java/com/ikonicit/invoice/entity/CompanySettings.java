package com.ikonicit.invoice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "company_settings")
public class CompanySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── Company Settings ─────────────────────────────
    // "Company name"
    @Column(name = "company_name")
    private String companyName;

    // "Company reg. no."
    @Column(name = "company_reg_no")
    private String companyRegNo;

    // "Vat no."
    @Column(name = "vat_no")
    private String vatNo;

    // "Company seat" — registered location of company
    @Column(name = "company_seat")
    private String companySeat;

    // "Does your company hold an F-tax certificate?"
    // F-tax = Swedish tax registration for self-employed
    @Column(name = "f_tax_certificate")
    private Boolean fTaxCertificate = true;

    // "Logo" — company logo URL
    @Column(name = "logo_url")
    private String logoUrl;

    // ─── Address ──────────────────────────────────────
    // "C/O"
    @Column(name = "care_of")
    private String careOf;

    // "Address"
    @Column(name = "address")
    private String address;

    // "Zip code"
    @Column(name = "zip_code")
    private String zipCode;

    // "City"
    @Column(name = "city")
    private String city;

    // "Country"
    @Column(name = "country")
    private String country;

    // ─── Contact Information ──────────────────────────
    // "Company email"
    @Column(name = "company_email")
    private String companyEmail;

    // "Website"
    @Column(name = "website")
    private String website;

    // "Phone"
    @Column(name = "phone")
    private String phone;

    // "Mobile phone"
    @Column(name = "mobile_phone")
    private String mobilePhone;

    // "Fax"
    @Column(name = "fax")
    private String fax;

    // ─── Payment Method ───────────────────────────────
    // "Add payment method" dropdown
    // Values: "bankgiro" | "plusgiro" | "bank_account" | "swish"
    @Column(name = "payment_method")
    private String paymentMethod;

    // Payment method details
    @Column(name = "bankgiro_number")
    private String bankgiroNumber;

    @Column(name = "plusgiro_number")
    private String plusgiroNumber;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "swish_number")
    private String swishNumber;

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