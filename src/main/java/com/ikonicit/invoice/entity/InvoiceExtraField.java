package com.ikonicit.invoice.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "invoice_extra_field")
public class InvoiceExtraField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    // Matches InvoiceForm's MORE_OPTIONS key, e.g. "extraFieldsLong",
    // "buyerPersonalId", "buyerVat", "reverseCharge", "threePartyTrade",
    // or one of the ROT group's sub-keys: "brfOrgNo",
    // "apartmentDesignation", "propertyDesignation".
    @Column(name = "field_key", nullable = false)
    private String fieldKey;

    @Column(name = "field_text", columnDefinition = "TEXT")
    private String fieldText;
}