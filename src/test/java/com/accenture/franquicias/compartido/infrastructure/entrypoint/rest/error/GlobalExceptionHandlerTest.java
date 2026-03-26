package com.accenture.franquicias.compartido.infrastructure.entrypoint.rest.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias del manejo global de excepciones de la capa HTTP.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void debeRetornar500HomogeneoCuandoOcurreUnErrorNoControlado() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/pruebas/error-interno").build()
        );

        ResponseEntity<ApiErrorResponse> respuesta = globalExceptionHandler.manejarErrorInesperado(
                new RuntimeException("Fallo tecnico"),
                exchange
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(respuesta.getBody())
                .isNotNull()
                .extracting(
                        ApiErrorResponse::status,
                        ApiErrorResponse::error,
                        ApiErrorResponse::message,
                        ApiErrorResponse::path
                )
                .containsExactly(
                        500,
                        "Internal Server Error",
                        "Error interno del servidor",
                        "/api/v1/pruebas/error-interno"
                );
        assertThat(respuesta.getBody().timestamp()).isNotNull();
    }

    @Test
    void debeRetornar400ConMensajeDeNegocioParaIllegalArgumentException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/v1/productos").build()
        );

        ResponseEntity<ApiErrorResponse> respuesta = globalExceptionHandler.manejarErrorNegocio(
                new IllegalArgumentException("El stock no puede ser negativo"),
                exchange
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody())
                .isNotNull()
                .extracting(
                        ApiErrorResponse::status,
                        ApiErrorResponse::error,
                        ApiErrorResponse::message,
                        ApiErrorResponse::path
                )
                .containsExactly(
                        400,
                        "Bad Request",
                        "El stock no puede ser negativo",
                        "/api/v1/productos"
                );
        assertThat(respuesta.getBody().timestamp()).isNotNull();
    }

    @Test
    void debeRetornar404CuandoLaRutaNoExiste() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/ruta-inexistente").build()
        );

        ResponseEntity<ApiErrorResponse> respuesta = globalExceptionHandler.manejarRutaNoEncontrada(
                new NoResourceFoundException("/api/v1/ruta-inexistente"),
                exchange
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(respuesta.getBody())
                .isNotNull()
                .extracting(
                        ApiErrorResponse::status,
                        ApiErrorResponse::error,
                        ApiErrorResponse::message,
                        ApiErrorResponse::path
                )
                .containsExactly(
                        404,
                        "Not Found",
                        "Recurso no encontrado",
                        "/api/v1/ruta-inexistente"
                );
        assertThat(respuesta.getBody().timestamp()).isNotNull();
    }
}
