package com.todo.api.exception;

import org.springframework.http.HttpStatus;

public class UsernameNotFoundException extends BusinessException {
    public UsernameNotFoundException(String message) {
        super(message, HttpStatus.CONFLICT); // 409 Conflict
    }
}