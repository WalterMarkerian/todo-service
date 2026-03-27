package com.todo.api.service;

import com.todo.api.exception.UserAlreadyExistsException;
import com.todo.api.model.dto.AuthResponse;
import com.todo.api.model.dto.LoginRequest;
import com.todo.api.model.dto.RegisterRequest;
import com.todo.api.model.enums.Role;
import com.todo.api.model.entity.User;
import com.todo.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userService.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("El nombre de usuario ya existe");
        }

        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        userService.save(user);
        return new AuthResponse(jwtService.generateToken(user));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        var user = userService.findByUsername(request.username());
        return new AuthResponse(jwtService.generateToken(user));
    }
}