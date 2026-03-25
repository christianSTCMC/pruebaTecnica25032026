package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto;

import java.util.UUID;

/**
 * Item de respuesta con datos de producto.
 *
 * @param productoId identificador del producto
 * @param productoNombre nombre del producto
 * @param stock stock del producto
 */
public record ProductoMayorStockPorSucursalResponse(
        UUID productoId,
        String productoNombre,
        Integer stock
) {
}
