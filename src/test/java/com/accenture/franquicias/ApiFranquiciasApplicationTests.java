package com.accenture.franquicias;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica que el bootstrap base del proyecto cargue sin adelantar logica de negocio.
 */
@SpringBootTest(properties = "spring.flyway.enabled=false")
class ApiFranquiciasApplicationTests {

    @Autowired
    private OpenAPI openAPI;

    @Test
    void debeCargarElContextoYLaConfiguracionDeOpenApi() {
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("API de Franquicias");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("v1");
    }
}
