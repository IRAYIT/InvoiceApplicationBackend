package com.ikonicit.invoice.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ClientResponseDTO {

    private Long id;
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
    private Boolean isActive;

    // Direct class reference — no nesting
    private ClientAddressDTO address;
    private ClientAddressDTO deliveryAddress;
    private ClientSettingsDTO settings;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}