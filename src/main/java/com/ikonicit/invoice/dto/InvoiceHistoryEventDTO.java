package com.ikonicit.invoice.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
public class InvoiceHistoryEventDTO {
    private String label;
    private LocalDateTime timestamp;
}