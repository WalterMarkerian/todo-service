package com.todo.api.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Modelo que representa una tarea en la lista")
public record TodoDto(
        @Schema(description = "ID único de la tarea", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @NotBlank(message = "El título no puede estar vacío")
        @Schema(description = "Resumen de la tarea a realizar", example = "Comprar café")
        String title,

        @Schema(description = "Detalle extendido de la tarea", example = "Ir al tostadero artesanal de la esquina")
        String description,

        @Schema(description = "Estado de la tarea", example = "false")
        @JsonProperty("completed") // <--- BLINDAJE: Asegura que el JSON "completed" entre acá
        Boolean completed
) {}