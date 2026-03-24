package com.todo.api.service;

import com.todo.api.model.dto.TodoDto;
import com.todo.api.model.entity.Todo;
import com.todo.api.mapper.TodoMapper;
import com.todo.api.repository.TodoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Crea el constructor para la inyección de dependencias
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    @Transactional(readOnly = true)
    public Page<TodoDto> listarTodosPaginado(Pageable pageable) {
        // El repositorio ya entiende Pageable por heredar de JpaRepository
        return todoRepository.findAll(pageable)
                .map(todoMapper::toDto);
    }

    @Transactional
    public TodoDto guardar(TodoDto dto) {
        Todo entity = todoMapper.toEntity(dto);
        Todo saved = todoRepository.save(entity);
        return todoMapper.toDto(saved);
    }

    @Transactional
    public TodoDto toggleEstado(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada con ID: " + id));

        todo.setCompleted(!todo.isCompleted());

        Todo actualizado = todoRepository.save(todo);
        return todoMapper.toDto(actualizado);
    }
}