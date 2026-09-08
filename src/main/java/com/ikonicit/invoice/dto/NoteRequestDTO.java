package com.ikonicit.invoice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteRequestDTO {

    private String subject;

    private String noteText;
}