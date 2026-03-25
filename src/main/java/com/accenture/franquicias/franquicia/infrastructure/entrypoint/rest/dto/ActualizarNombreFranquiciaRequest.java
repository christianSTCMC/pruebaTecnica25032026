package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request para actualizar el nombre de una franquicia.
 *
 * @param nombre nuevo nombre de la franquicia, obligatorio y no vacio
 */
public record ActualizarNombreFranquiciaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre
) {
}
