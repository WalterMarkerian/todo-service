package com.todo.api.mapper;

import com.todo.api.model.dto.TodoDto;
import com.todo.api.model.entity.Todo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // Permite inyectarlo con @Autowired
public interface TodoMapper {

    // Convierte de Entidad a Record (DTO)
    TodoDto toDto(Todo todo);

    // Convierte de Record (DTO) a Entidad
    @Mapping(target = "id", ignore = true) // El ID lo genera la DB, lo ignoramos al crear
    Todo toEntity(TodoDto todoDto);
}