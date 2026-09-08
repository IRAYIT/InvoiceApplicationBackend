package com.ikonicit.invoice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TodoResponseDTO {
    private Long id;
    private Long clientId;
    private String description;
    private String assignedTo;
    private String priority;
    private String productService;
    private LocalDate deadline;
    private Boolean done;
    private Double hours;
    private Double unbilled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}