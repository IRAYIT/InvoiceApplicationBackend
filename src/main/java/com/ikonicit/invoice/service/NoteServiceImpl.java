package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.NoteRequestDTO;
import com.ikonicit.invoice.dto.NoteResponseDTO;
import com.ikonicit.invoice.entity.Client;
import com.ikonicit.invoice.entity.Note;
import com.ikonicit.invoice.repository.ClientRepository;
import com.ikonicit.invoice.repository.NoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final ClientRepository clientRepository;
    @Override
    @Transactional
    public NoteResponseDTO create(Long clientId, NoteRequestDTO request) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Client not found with id: " + clientId
                        )
                );

        if (request.getSubject() == null ||
                request.getSubject().trim().isEmpty()) {

            throw new RuntimeException("Subject is required.");
        }

        if (request.getNoteText() == null ||
                request.getNoteText().trim().isEmpty()) {

            throw new RuntimeException("Note text is required.");
        }

        Note note = new Note();

        note.setSubject(request.getSubject().trim());
        note.setNoteText(request.getNoteText());
        note.setClient(client);

        Note saved = noteRepository.save(note);

        return toResponse(saved);
    }
    @Override
    @Transactional
    public NoteResponseDTO update(Long noteId, NoteRequestDTO request) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Note not found with id: " + noteId
                        )
                );

        if (request.getSubject() == null ||
                request.getSubject().trim().isEmpty()) {

            throw new RuntimeException("Subject is required.");
        }

        if (request.getNoteText() == null ||
                request.getNoteText().trim().isEmpty()) {

            throw new RuntimeException("Note text is required.");
        }

        note.setSubject(request.getSubject().trim());
        note.setNoteText(request.getNoteText());

        /*
         * @PreUpdate automatically updates updatedAt.
         */
        Note updated = noteRepository.save(note);

        return toResponse(updated);
    }
    @Override
    public NoteResponseDTO getById(Long noteId) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Note not found with id: " + noteId
                        )
                );

        return toResponse(note);
    }

    @Override
    public List<NoteResponseDTO> getByClientId(
            Long clientId,
            String search
    ) {

        // Make sure client exists
        clientRepository.findById(clientId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Client not found with id: " + clientId
                        )
                );

        List<Note> notes;

        if (search != null && !search.trim().isEmpty()) {

            notes =
                    noteRepository
                            .findByClientIdAndSubjectContainingIgnoreCaseOrderByUpdatedAtDesc(
                                    clientId,
                                    search.trim()
                            );

        } else {

            notes =
                    noteRepository
                            .findByClientIdOrderByUpdatedAtDesc(clientId);
        }

        return notes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public void delete(Long noteId) {

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Note not found with id: " + noteId
                        )
                );

        noteRepository.delete(note);
    }
    private NoteResponseDTO toResponse(Note note) {

        return NoteResponseDTO.builder()
                .id(note.getId())
                .clientId(
                        note.getClient() != null
                                ? note.getClient().getId()
                                : null
                )
                .subject(note.getSubject())
                .noteText(note.getNoteText())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}