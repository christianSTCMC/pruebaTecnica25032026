package com.accenture.franquicias.producto.infrastructure.entrypoint.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request para actualizar el stock de un producto.
 *
 * @param stock nuevo valor de stock, obligatorio y no negativo
 */
public record ActualizarStockProductoRequest(
        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
        Integer stock
) {
}
