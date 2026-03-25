package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request para crear franquicias mediante API REST.
 *
 * @param nombre nombre de la franquicia, obligatorio y no vacio
 */
public record CrearFranquiciaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre
) {
}
