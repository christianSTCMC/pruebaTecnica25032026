package com.accenture.franquicias.producto.infrastructure.entrypoint.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request para actualizar el stock de un producto.
 *
 * @param stock nuevo valor de stock, obligatorio, no negativo y dentro del limite del tipo INT en DB
 */
public record ActualizarStockProductoRequest(
        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
        @Max(value = 2147483647L, message = "El stock no puede ser mayor a 2147483647")
        Long stock
) {
}
