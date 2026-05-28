package com.ikonicit.invoice.dto;

import lombok.*;

@Getter
@Setter
public class ClientRequestDTO {

    private String clientType;

    // Company fields
    private String company;
    private String companyRegNo;
    private String vatNo;

    // Person fields
    private String firstName;
    private String lastName;
    private String personalIdNo;

    // Common
    private String email;
    private String number;

    // Direct class reference — no nesting
    private ClientAddressDTO address;
    private ClientAddressDTO deliveryAddress;
    private ClientSettingsDTO settings;
}