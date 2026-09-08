package com.ikonicit.invoice.repository;

import com.ikonicit.invoice.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByClientIdOrderByCreatedAtDesc(Long clientId);
}