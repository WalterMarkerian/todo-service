package com.todo.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Deshabilitar CSRF (necesario para APIs REST que usan Postman/Swagger)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configurar permisos de rutas
                .authorizeHttpRequests(auth -> auth
                        // Permitir acceso público a Swagger y documentación
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Por ahora permitimos todo para que pruebes la lógica,
                        // luego cambiaremos .permitAll() por .authenticated()
                        .anyRequest().permitAll()
                )

                // 3. Habilitar autenticación básica (para cuando quieras proteger la API)
                .httpBasic(withDefaults());

        return http.build();
    }
}