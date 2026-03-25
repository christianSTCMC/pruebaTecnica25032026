package com.accenture.franquicias.sucursal.infrastructure.entrypoint.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request para actualizar el nombre de una sucursal.
 *
 * @param nombre nuevo nombre de la sucursal, obligatorio y no vacio
 */
public record ActualizarNombreSucursalRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre
) {
}
