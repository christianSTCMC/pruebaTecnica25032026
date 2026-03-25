package com.accenture.franquicias.compartido.infrastructure.entrypoint.rest.error;

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
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
