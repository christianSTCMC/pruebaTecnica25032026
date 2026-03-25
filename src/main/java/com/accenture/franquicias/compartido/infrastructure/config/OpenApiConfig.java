package com.accenture.franquicias.compartido.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Centraliza la configuracion minima de OpenAPI para exponer Swagger desde la
 * primera fase sin depender todavia de controladores de negocio.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI franquiciasOpenApi(
            @Value("${api.info.title:API de Franquicias}") String title,
            @Value("${api.info.description:API para gestionar franquicias, sucursales y productos}") String description,
            @Value("${api.info.version:v1}") String version) {
        return new OpenAPI().info(new Info()
                .title(title)
                .description(description)
                .version(version));
    }
}
