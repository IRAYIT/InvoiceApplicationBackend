package com.ikonicit.invoice.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "client_settings")
public class ClientSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // "Send invoices by" radio buttons
    // Values: "email" | "epost_sms" | "letter" | "e_invoice"
    // Note: "e_invoice" only available for Company type
    @Column(name = "invoice_delivery_method")
    private String invoiceDeliveryMethod = "email";

    // "Always attach a PDF copy in emails" checkbox
    @Column(name = "email_attach_pdf")
    private Boolean emailAttachPdf = false;
}