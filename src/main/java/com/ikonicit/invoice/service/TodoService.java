package com.ikonicit.invoice.service;

import com.ikonicit.invoice.dto.TodoRequestDTO;
import com.ikonicit.invoice.dto.TodoResponseDTO;
import java.util.List;

public interface TodoService {
    List<TodoResponseDTO> getTodosByClient(Long clientId);
    TodoResponseDTO createTodo(Long clientId, TodoRequestDTO req);
    TodoResponseDTO updateTodo(Long todoId, TodoRequestDTO req);
    TodoResponseDTO toggleDone(Long todoId, Boolean done);
    void deleteTodo(Long todoId);
}