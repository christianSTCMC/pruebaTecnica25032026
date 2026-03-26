package com.accenture.franquicias.compartido.infrastructure.logging;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Verifica que las peticiones HTTP salgan con un identificador util para correlacion.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.flyway.enabled=false")
@AutoConfigureWebTestClient
class RequestLoggingFilterIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void debePropagarElRequestIdEnLaRespuesta() {
        webTestClient.get()
                .uri("/v3/api-docs")
                .header("X-Request-Id", "req-prueba-001")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Request-Id", "req-prueba-001");
    }
}
