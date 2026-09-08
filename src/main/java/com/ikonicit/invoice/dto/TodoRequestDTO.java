package com.ikonicit.invoice.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class TodoRequestDTO {
    private String description;
    private String assignedTo;
    private String priority;
    private String productService;
    private LocalDate deadline;
    private Boolean done;
    private Double hours;
    private Double unbilled;
}