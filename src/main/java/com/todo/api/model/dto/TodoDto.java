package com.todo.api.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todo.api.model.enums.TodoStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Modelo que representa una tarea en la lista")
public record TodoDto(
        @Schema(description = "ID único de la tarea", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @NotBlank(message = "El título no puede estar vacío")
        @Schema(description = "Resumen de la tarea a realizar", example = "Comprar café")
        String title,

        @Schema(description = "Detalle extendido de la tarea", example = "Ir al tostadero artesanal")
        String description,

        @Schema(description = "Estado actual de la tarea", example = "PENDIENTE")
        TodoStatus status,

        @Schema(description = "Mantenemos por compatibilidad", example = "false")
        @JsonProperty("completed")
        Boolean completed
) {}