package com.ikonicit.invoice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDTO {
    private Long id;
    private Long productId;
    private String description;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal taxPercent;
    private BigDecimal deliveredQuantity;
    private BigDecimal lineSubtotal;
    private BigDecimal lineTax;
    private BigDecimal lineTotal;
}