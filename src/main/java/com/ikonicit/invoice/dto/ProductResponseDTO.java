package com.ikonicit.invoice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String unit;
    private String productCode;

    // Product group details
    private Long productGroupId;
    private String productGroupName;    // show group name in response

    // Pricing
    private BigDecimal price;           // excl. VAT
    private BigDecimal tax;             // VAT %
    private BigDecimal priceInclTax;    // calculated in service

    // ROT / RUT / Green tech
    private Boolean rotRutGreenTech;

    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}