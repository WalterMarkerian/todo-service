package com.todo.api.service;

import com.todo.api.exception.ResourceNotFoundException;
import com.todo.api.exception.UnauthorizedAccessException;
import com.todo.api.mapper.TodoMapper;
import com.todo.api.model.dto.TodoDto;
import com.todo.api.model.entity.Todo;
import com.todo.api.model.entity.User;
import com.todo.api.model.enums.TodoStatus;
import com.todo.api.repository.TodoRepository;
import com.todo.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final TodoMapper todoMapper;

    @Transactional(readOnly = true)
    public Page<TodoDto> listarTodosPaginado(Pageable pageable) {
        return todoRepository.findByUser_Username(getCurrentUsername(), pageable)
                .map(todoMapper::toDto);
    }

    @Transactional
    public TodoDto guardar(TodoDto dto) {
        User user = userRepository.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Todo entity = todoMapper.toEntity(dto);
        entity.setUser(user);

        // Si el DTO no trae estado, nace como PENDIENTE
        if (entity.getStatus() == null) {
            entity.setStatus(TodoStatus.PENDIENTE);
        }

        return todoMapper.toDto(todoRepository.save(entity));
    }

    @Transactional
    public TodoDto toggleEstado(Long id) {
        Todo todo = getTodoIfOwner(id);

        // Si es PENDIENTE pasa a COMPLETADA, y viceversa.
        if (todo.getStatus() == TodoStatus.PENDIENTE) {
            todo.setStatus(TodoStatus.COMPLETADA);
        } else {
            todo.setStatus(TodoStatus.PENDIENTE);
        }

        return todoMapper.toDto(todoRepository.save(todo));
    }

    // Nuevo método: Cambiar a un estado específico (ej: EN_PROGRESO)
    @Transactional
    public TodoDto actualizarEstado(Long id, TodoStatus nuevoEstado) {
        Todo todo = getTodoIfOwner(id);
        todo.setStatus(nuevoEstado);
        return todoMapper.toDto(todoRepository.save(todo));
    }

    @Transactional
    public void eliminar(Long id) {
        Todo todo = getTodoIfOwner(id);
        todoRepository.delete(todo);
    }

    private Todo getTodoIfOwner(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));

        if (!todo.getUser().getUsername().equals(getCurrentUsername())) {
            throw new UnauthorizedAccessException("No tienes permiso sobre esta tarea");
        }
        return todo;
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new UnauthorizedAccessException("Sesión no válida o expirada");
        }
        return auth.getName();
    }
}