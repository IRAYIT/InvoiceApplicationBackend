package com.ikonicit.invoice.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDTO {

    private Long productId; // null for a manually typed line item

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
    private BigDecimal quantity;

    private String unit;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Unit price cannot be negative")
    private BigDecimal unitPrice;

    @NotNull(message = "Tax percent is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tax percent cannot be negative")
    private BigDecimal taxPercent;
}
