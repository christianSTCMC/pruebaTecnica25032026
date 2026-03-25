package com.accenture.franquicias.compartido.infrastructure.entrypoint.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Verifica que el contrato OpenAPI publicado cubra los endpoints y errores clave.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.flyway.enabled=false")
@AutoConfigureWebTestClient
class OpenApiDocumentationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void debePublicarEndpointsYEsquemaDeErrorEnOpenApi() {
        webTestClient.get()
                .uri("/v3/api-docs")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.paths['/api/v1/franquicias']").exists()
                .jsonPath("$.paths['/api/v1/franquicias/{franquiciaId}/nombre']").exists()
                .jsonPath("$.paths['/api/v1/franquicias/{franquiciaId}/sucursales']").exists()
                .jsonPath("$.paths['/api/v1/sucursales/{sucursalId}/nombre']").exists()
                .jsonPath("$.paths['/api/v1/sucursales/{sucursalId}/productos']").exists()
                .jsonPath("$.paths['/api/v1/sucursales/{sucursalId}/productos/{productoId}']").exists()
                .jsonPath("$.paths['/api/v1/productos/{productoId}/nombre']").exists()
                .jsonPath("$.paths['/api/v1/productos/{productoId}/stock']").exists()
                .jsonPath("$.paths['/api/v1/franquicias/{franquiciaId}/productos/mayor-stock-por-sucursal']").exists()
                .jsonPath("$.paths['/api/v1/franquicias/{franquiciaId}/productos/mayor-stock-por-sucursal/listado']").exists()
                .jsonPath("$.paths['/api/v1/franquicias'].post.responses['409']").exists()
                .jsonPath("$.paths['/api/v1/franquicias/{franquiciaId}/nombre'].patch.responses['409']").exists()
                .jsonPath("$.paths['/api/v1/franquicias/{franquiciaId}/sucursales'].post.responses['409']").exists()
                .jsonPath("$.paths['/api/v1/sucursales/{sucursalId}/nombre'].patch.responses['409']").exists()
                .jsonPath("$.paths['/api/v1/franquicias/{franquiciaId}/nombre'].patch.responses['404']").exists()
                .jsonPath("$.paths['/api/v1/sucursales/{sucursalId}/nombre'].patch.responses['404']").exists()
                .jsonPath("$.paths['/api/v1/productos/{productoId}/nombre'].patch.responses['409']").exists()
                .jsonPath("$.paths['/api/v1/productos/{productoId}/stock'].patch.responses['400']").exists()
                .jsonPath("$.paths['/api/v1/productos/{productoId}/stock'].patch.responses['404']").exists()
                .jsonPath("$.paths['/api/v1/sucursales/{sucursalId}/productos'].post.responses['409']").exists()
                .jsonPath("$.components.schemas.ApiErrorResponse").exists()
                .jsonPath("$.components.schemas.ApiErrorResponse.properties.timestamp.type").isEqualTo("string")
                .jsonPath("$.components.schemas.ApiErrorResponse.properties.status.type").isEqualTo("integer")
                .jsonPath("$.components.schemas.ApiErrorResponse.properties.message.type").isEqualTo("string");
    }
}
