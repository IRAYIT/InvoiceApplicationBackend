package com.ikonicit.invoice.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class EstimateRequestDTO {

    // Which client
    private Long clientId;

    // When estimate is created
    private LocalDate issueDate;

    // When estimate expires
    private LocalDate validUntil;

    // Notes/message to client
    private String notes;

    // Line items
    private List<EstimateItemRequestDTO> items;
}