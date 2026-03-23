package com.todo.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Lista de Tareas")
                        .version("1.0")
                        .description("Documentación de la API de Tareas usando Spring Boot 3 y Java 21")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}