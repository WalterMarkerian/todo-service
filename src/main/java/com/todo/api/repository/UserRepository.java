package com.todo.api.repository;


import com.todo.api.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Método crucial para el login: busca al usuario por su nombre
    Optional<User> findByUsername(String username);

    // Opcional: para validar si el email ya existe en el registro
    Boolean existsByUsername(String username);
}