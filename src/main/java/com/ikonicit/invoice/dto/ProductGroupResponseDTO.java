package com.ikonicit.invoice.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProductGroupResponseDTO {

    private Long id;
    private String name;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}