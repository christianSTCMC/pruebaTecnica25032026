package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto;

import java.util.UUID;

/**
 * Item de respuesta con el producto ganador para una sucursal.
 *
 * @param sucursalId identificador de la sucursal
 * @param sucursalNombre nombre de la sucursal
 * @param productoId identificador del producto seleccionado
 * @param productoNombre nombre del producto seleccionado
 * @param stock stock del producto seleccionado
 */
public record ProductoMayorStockPorSucursalResponse(
        UUID sucursalId,
        String sucursalNombre,
        UUID productoId,
        String productoNombre,
        Integer stock
) {
}
