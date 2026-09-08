package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.TodoRequestDTO;
import com.ikonicit.invoice.dto.TodoResponseDTO;
import com.ikonicit.invoice.entity.Client;
import com.ikonicit.invoice.entity.Todo;
import com.ikonicit.invoice.repository.ClientRepository;
import com.ikonicit.invoice.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final ClientRepository clientRepository;

    @Override
    public List<TodoResponseDTO> getTodosByClient(Long clientId) {
        return todoRepository.findByClientIdOrderByCreatedAtDesc(clientId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TodoResponseDTO createTodo(Long clientId, TodoRequestDTO req) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + clientId));

        Todo todo = Todo.builder()
                .client(client)
                .description(req.getDescription())
                .assignedTo(req.getAssignedTo() != null ? req.getAssignedTo() : "Everybody")
                .priority(req.getPriority() != null ? req.getPriority() : "Low")
                .productService(req.getProductService())
                .deadline(req.getDeadline())
                .done(req.getDone() != null ? req.getDone() : false)
                .hours(req.getHours() != null ? req.getHours() : 0.0)
                .unbilled(req.getUnbilled() != null ? req.getUnbilled() : 0.0)
                .build();

        return toResponse(todoRepository.save(todo));
    }

    @Override
    @Transactional
    public TodoResponseDTO updateTodo(Long todoId, TodoRequestDTO req) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + todoId));

        if (req.getDescription() != null) todo.setDescription(req.getDescription());
        if (req.getAssignedTo() != null) todo.setAssignedTo(req.getAssignedTo());
        if (req.getPriority() != null) todo.setPriority(req.getPriority());
        if (req.getProductService() != null) todo.setProductService(req.getProductService());
        if (req.getDeadline() != null) todo.setDeadline(req.getDeadline());
        if (req.getDone() != null) todo.setDone(req.getDone());
        if (req.getHours() != null) todo.setHours(req.getHours());
        if (req.getUnbilled() != null) todo.setUnbilled(req.getUnbilled());

        return toResponse(todoRepository.save(todo));
    }

    @Override
    @Transactional
    public TodoResponseDTO toggleDone(Long todoId, Boolean done) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + todoId));
        todo.setDone(done);
        return toResponse(todoRepository.save(todo));
    }

    @Override
    @Transactional
    public void deleteTodo(Long todoId) {
        if (!todoRepository.existsById(todoId)) {
            throw new RuntimeException("Todo not found with id: " + todoId);
        }
        todoRepository.deleteById(todoId);
    }

    private TodoResponseDTO toResponse(Todo t) {
        return TodoResponseDTO.builder()
                .id(t.getId())
                .clientId(t.getClient() != null ? t.getClient().getId() : null)
                .description(t.getDescription())
                .assignedTo(t.getAssignedTo())
                .priority(t.getPriority())
                .productService(t.getProductService())
                .deadline(t.getDeadline())
                .done(t.getDone())
                .hours(t.getHours())
                .unbilled(t.getUnbilled())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}