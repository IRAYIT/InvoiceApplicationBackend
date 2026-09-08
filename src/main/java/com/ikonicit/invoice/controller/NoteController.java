package com.ikonicit.invoice.controller;

import com.ikonicit.invoice.dto.NoteRequestDTO;
import com.ikonicit.invoice.dto.NoteResponseDTO;
import com.ikonicit.invoice.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping("/{clientId}/notes")
    public ResponseEntity<NoteResponseDTO> createNote(
            @PathVariable Long clientId,
            @RequestBody NoteRequestDTO request
    ) {

        return ResponseEntity.ok(
                noteService.create(clientId, request)
        );
    }
    @GetMapping("/{clientId}/notes")
    public ResponseEntity<List<NoteResponseDTO>> getNotes(
            @PathVariable Long clientId,
            @RequestParam(required = false) String search
    ) {

        return ResponseEntity.ok(
                noteService.getByClientId(clientId, search)
        );
    }
    @GetMapping("/notes/{noteId}")
    public ResponseEntity<NoteResponseDTO> getNote(
            @PathVariable Long noteId
    ) {

        return ResponseEntity.ok(
                noteService.getById(noteId)
        );
    }
    @PutMapping("/notes/{noteId}")
    public ResponseEntity<NoteResponseDTO> updateNote(
            @PathVariable Long noteId,
            @RequestBody NoteRequestDTO request
    ) {

        return ResponseEntity.ok(
                noteService.update(noteId, request)
        );
    }
    @DeleteMapping("/notes/{noteId}")
    public ResponseEntity<String> deleteNote(
            @PathVariable Long noteId
    ) {

        noteService.delete(noteId);

        return ResponseEntity.ok(
                "Note deleted successfully."
        );
    }
}