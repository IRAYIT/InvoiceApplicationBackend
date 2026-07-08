package com.ikonicit.invoice.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class EstimateItemResponseDTO {

    private Long id;

    // Product reference
    private Long productId;

    // Item details
    private String description;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal unitPrice;

    // Tax
    private BigDecimal taxPercent;
    private BigDecimal taxAmount;     // calculated

    // Total for this line
    private BigDecimal lineTotal;     // quantity * unitPrice
}