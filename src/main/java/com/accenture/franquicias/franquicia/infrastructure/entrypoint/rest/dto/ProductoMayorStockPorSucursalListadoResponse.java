package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto;

import java.util.UUID;

/**
 * Item de respuesta en formato plano para identificar producto y sucursal.
 *
 * @param sucursalId identificador de la sucursal propietaria del producto
 * @param sucursalNombre nombre de la sucursal propietaria del producto
 * @param productoId identificador del producto con mayor stock
 * @param productoNombre nombre del producto con mayor stock
 * @param stock stock del producto ganador en la sucursal
 */
public record ProductoMayorStockPorSucursalListadoResponse(
        UUID sucursalId,
        String sucursalNombre,
        UUID productoId,
        String productoNombre,
        Integer stock
) {
}
