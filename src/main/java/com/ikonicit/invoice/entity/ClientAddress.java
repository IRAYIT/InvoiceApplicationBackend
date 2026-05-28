package com.ikonicit.invoice.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "client_address")
public class ClientAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // "C/O" field
    @Column(name = "care_of")
    private String careOf;

    // "Address" field
    @Column(name = "street_address")
    private String streetAddress;

    // "Zip code" field
    @Column(name = "zip_code")
    private String zipCode;

    // "City" field
    @Column(name = "city")
    private String city;

    // "Country" dropdown — e.g. "India", stored as ISO code "IN"
    @Column(name = "country")
    private String country;
}