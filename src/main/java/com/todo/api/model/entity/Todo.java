package com.todo.api.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "todos")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private boolean completed;
}