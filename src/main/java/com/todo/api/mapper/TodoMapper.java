package com.todo.api.mapper;

import com.todo.api.model.dto.TodoDto;
import com.todo.api.model.entity.Todo;
import com.todo.api.model.enums.TodoStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TodoMapper {

    @Mapping(target = "completed", source = "todo", qualifiedByName = "mapCompleted")
    TodoDto toDto(Todo todo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Todo toEntity(TodoDto todoDto);

    // Lógica para calcular el booleano 'completed' basado en el Enum
    @Named("mapCompleted")
    default Boolean mapCompleted(Todo todo) {
        return todo.getStatus() == TodoStatus.COMPLETADA;
    }
}