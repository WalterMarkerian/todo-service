package com.todo.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Permitir todas las rutas de la API
                .allowedOrigins("http://localhost:5173/") // El origen de tu React
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Incluimos OPTIONS
                .allowedHeaders("*") // Permitir todos los headers (Content-Type, Authorization, etc.)
                .allowCredentials(true);
    }
}