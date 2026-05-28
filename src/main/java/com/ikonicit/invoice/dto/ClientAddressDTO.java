package com.ikonicit.invoice.dto;


import lombok.*;

@Getter
@Setter
public class ClientAddressDTO {

    private String careOf;
    private String streetAddress;
    private String zipCode;
    private String city;
    private String country;
}
