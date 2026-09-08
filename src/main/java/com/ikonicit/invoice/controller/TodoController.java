package com.ikonicit.invoice.controller;

import com.ikonicit.invoice.dto.TodoRequestDTO;
import com.ikonicit.invoice.dto.TodoResponseDTO;
import com.ikonicit.invoice.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/todos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<TodoResponseDTO>> getByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(todoService.getTodosByClient(clientId));
    }

    @PostMapping("/client/{clientId}")
    public ResponseEntity<TodoResponseDTO> create(
            @PathVariable Long clientId,
            @RequestBody TodoRequestDTO requestDTO) {
        return new ResponseEntity<>(todoService.createTodo(clientId, requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<TodoResponseDTO> update(
            @PathVariable Long todoId,
            @RequestBody TodoRequestDTO requestDTO) {
        return ResponseEntity.ok(todoService.updateTodo(todoId, requestDTO));
    }

    @PatchMapping("/{todoId}/status")
    public ResponseEntity<TodoResponseDTO> toggleStatus(
            @PathVariable Long todoId,
            @RequestBody Map<String, Boolean> statusMap) {
        Boolean done = statusMap.getOrDefault("done", false);
        return ResponseEntity.ok(todoService.toggleDone(todoId, done));
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> delete(@PathVariable Long todoId) {
        todoService.deleteTodo(todoId);
        return ResponseEntity.noContent().build();
    }
}