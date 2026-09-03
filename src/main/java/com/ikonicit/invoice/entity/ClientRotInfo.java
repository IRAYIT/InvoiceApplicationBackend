package com.ikonicit.invoice.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "client_rot_info")
public class ClientRotInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // "Apartment designation" — Swedish "lägenhetsbeteckning"
    // Required for ROT/RUT tax deduction claims on apartments
    @Column(name = "apartment_designation")
    private String apartmentDesignation;

    // "Property designation" — Swedish "fastighetsbeteckning"
    // Required for ROT tax deduction claims on houses/property
    @Column(name = "property_designation")
    private String propertyDesignation;

    // "Assoc. corp ID no." — housing association org number,
    // needed when the property is owned via a housing co-op (BRF)
    @Column(name = "assoc_corp_id_no")
    private String assocCorpIdNo;
}