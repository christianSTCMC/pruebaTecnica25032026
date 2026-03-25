package com.accenture.franquicias.compartido.infrastructure.entrypoint.rest.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Estructura estandar de error usada por el API para respuestas 4xx.
 *
 * @param timestamp fecha y hora UTC del error
 * @param status codigo HTTP
 * @param error descripcion corta del estado HTTP
 * @param message detalle de negocio o validacion
 * @param path ruta solicitada que produjo el error
 */
@Schema(
        name = "ApiErrorResponse",
        description = "Formato homogeneo de error para respuestas 4xx y 5xx"
)
public record ApiErrorResponse(
        @Schema(description = "Fecha y hora UTC en que ocurrio el error", example = "2026-03-25T12:30:00Z")
        Instant timestamp,
        @Schema(description = "Codigo HTTP de la respuesta", example = "400")
        int status,
        @Schema(description = "Descripcion corta del estado HTTP", example = "Bad Request")
        String error,
        @Schema(description = "Detalle legible del error", example = "El stock debe ser mayor o igual a 0")
        String message,
        @Schema(description = "Ruta HTTP que produjo el error", example = "/api/v1/productos/3eb1f3b0-a89d-4ea2-a386-264a7a37e0d0/stock")
        String path
) {
}
