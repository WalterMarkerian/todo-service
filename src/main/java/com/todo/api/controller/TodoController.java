package com.todo.api.controller;

import com.todo.api.model.dto.TodoDto;
import com.todo.api.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Tag(name = "Tareas", description = "Operaciones para la gestión de la lista de tareas")
public class TodoController {

    private final TodoService todoService;

    @Operation(summary = "Obtener lista paginada", description = "Retorna una página de tareas con soporte para filtros de ordenamiento.")
    @GetMapping
    public ResponseEntity<Page<TodoDto>> getAll(
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(todoService.listarTodosPaginado(pageable));
    }

    @Operation(summary = "Crear nueva tarea", description = "Registra una tarea en la base de datos y retorna el objeto creado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "21", description = "Tarea creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<TodoDto> create(@Valid @RequestBody TodoDto dto) {
        return new ResponseEntity<>(todoService.guardar(dto), HttpStatus.CREATED);
    }
}