package com.accenture.franquicias.producto.infrastructure.entrypoint.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request para crear productos dentro de una sucursal.
 *
 * @param nombre nombre del producto, obligatorio y no vacio
 * @param stock stock inicial del producto, obligatorio y no negativo
 */
public record CrearProductoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
        Integer stock
) {
}
