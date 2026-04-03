package com.todo.api.controller;

import com.todo.api.service.AuthService;
import com.todo.api.model.dto.AuthResponse;
import com.todo.api.model.dto.LoginRequest;
import com.todo.api.model.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService; // Ver paso 3

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        System.out.println(">>> ¡LLEGÓ LA PETICIÓN! Usuario: " + request.username());
        return ResponseEntity.ok(authService.login(request));
    }
}