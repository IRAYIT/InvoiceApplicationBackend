package com.ikonicit.invoice.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
public class EstimateItemRequestDTO {

    // Optional — if picked from product list
    private Long productId;

    // Product/service name
    private String description;

    // How many units
    private BigDecimal quantity;

    // e.g. "hr", "st", "kg"
    private String unit;

    // Price per unit excluding VAT
    private BigDecimal unitPrice;

    // VAT % e.g. 25.00
    private BigDecimal taxPercent;
}