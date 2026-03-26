package com.accenture.franquicias.producto.infrastructure.entrypoint.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request para actualizar el nombre de un producto.
 *
 * @param nombre nuevo nombre del producto, obligatorio y no vacio
 */
public record ActualizarNombreProductoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre
) {
}
