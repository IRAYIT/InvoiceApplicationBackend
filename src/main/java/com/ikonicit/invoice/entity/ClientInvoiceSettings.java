package com.ikonicit.invoice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "client_invoice_settings")
public class ClientInvoiceSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Checkbox "Payment terms" + number input, e.g. 30
    @Column(name = "payment_terms_days")
    private Integer paymentTermsDays;

    // Checkbox "Language" + dropdown, e.g. "sv", "en"
    @Column(name = "invoice_language")
    private String invoiceLanguage;

    // Checkbox "Currency" + dropdown, e.g. "SEK", "INR"
    @Column(name = "currency")
    private String currency;

    // Checkbox "VAT for new rows" + %, e.g. 25.00
    @Column(name = "default_vat_percent", precision = 5, scale = 2)
    private BigDecimal defaultVatPercent;

    // Checkbox "Discount" + %, e.g. 0.00
    @Column(name = "default_discount_percent", precision = 5, scale = 2)
    private BigDecimal defaultDiscountPercent;
}