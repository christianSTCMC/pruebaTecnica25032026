package com.accenture.franquicias.franquicia.application.dto;

import java.util.UUID;

/**
 * Representa el producto con mayor stock para una sucursal puntual.
 *
 * @param sucursalId identificador de la sucursal
 * @param sucursalNombre nombre de la sucursal
 * @param productoId identificador del producto ganador
 * @param productoNombre nombre del producto ganador
 * @param stock stock del producto ganador
 */
public record ProductoMayorStockPorSucursalResultado(
        UUID sucursalId,
        String sucursalNombre,
        UUID productoId,
        String productoNombre,
        Integer stock
) {
}
