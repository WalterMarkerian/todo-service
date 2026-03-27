package com.todo.api.controller;

import com.todo.api.model.dto.TodoDto;
import com.todo.api.model.enums.TodoStatus;
import com.todo.api.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
@Tag(name = "Tareas", description = "Operaciones para la gestión de la lista de tareas")
public class TodoController {

    private final TodoService todoService;

    @Operation(summary = "Obtener lista de tareas paginada del usuario autenticado")
    @GetMapping
    public ResponseEntity<Page<TodoDto>> getAll(
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(todoService.listarTodosPaginado(pageable));
    }

    @Operation(summary = "Crear una nueva tarea vinculada al usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarea creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<TodoDto> create(@Valid @RequestBody TodoDto dto) {
        return new ResponseEntity<>(todoService.guardar(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Alternar entre PENDIENTE y COMPLETADA")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado alternado con éxito"),
            @ApiResponse(responseCode = "403", description = "No tienes permiso sobre esta tarea"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TodoDto> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.toggleEstado(id));
    }

    @Operation(summary = "Actualizar a un estado específico (PENDIENTE, EN_PROGRESO, COMPLETADA)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "400", description = "Estado no válido"),
            @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<TodoDto> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado de la tarea") @RequestParam TodoStatus status) {
        return ResponseEntity.ok(todoService.actualizarEstado(id, status));
    }

    @Operation(summary = "Eliminar una tarea")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        todoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}