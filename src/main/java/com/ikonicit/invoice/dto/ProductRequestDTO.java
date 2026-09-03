package com.ikonicit.invoice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequestDTO {

    // "Name" field
    private String name;

    // "Unit" field — e.g. "hr", "st", "kg", "day"
    private String unit;

    // "Product code" field — e.g. "WD-001"
    private String productCode;

    // "Product group" — send only the ID
    // Frontend sends group id from dropdown
    private Long productGroupId;

    // "Price" — excluding VAT
    private BigDecimal price;

    // "Tax" — VAT percentage e.g. 25.00
    private BigDecimal tax;

    // "ROT / RUT / Green tech" checkbox
    private Boolean rotRutGreenTech = false;

    // Note: priceInclTax is NOT here
    // Because it is calculated in service
    // price + (price * tax / 100)
}