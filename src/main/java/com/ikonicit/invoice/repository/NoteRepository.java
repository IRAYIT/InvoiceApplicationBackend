package com.ikonicit.invoice.repository;

import com.ikonicit.invoice.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByClientIdOrderByUpdatedAtDesc(Long clientId);

    List<Note> findByClientIdAndSubjectContainingIgnoreCaseOrderByUpdatedAtDesc(
            Long clientId,
            String subject
    );
}