package com.accenture.franquicias.sucursal.infrastructure.entrypoint.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request para crear sucursales por API REST.
 *
 * @param nombre nombre de la sucursal, obligatorio y no vacio
 */
public record CrearSucursalRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre
) {
}
