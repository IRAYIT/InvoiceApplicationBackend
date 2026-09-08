package com.ikonicit.invoice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NoteResponseDTO {

    private Long id;

    private Long clientId;

    private String subject;

    private String noteText;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}