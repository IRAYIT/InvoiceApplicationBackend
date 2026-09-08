package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.NoteRequestDTO;
import com.ikonicit.invoice.dto.NoteResponseDTO;

import java.util.List;

public interface NoteService {

    NoteResponseDTO create(Long clientId, NoteRequestDTO request);

    NoteResponseDTO update(Long noteId, NoteRequestDTO request);

    List<NoteResponseDTO> getByClientId(Long clientId, String search);

    NoteResponseDTO getById(Long noteId);

    void delete(Long noteId);
}