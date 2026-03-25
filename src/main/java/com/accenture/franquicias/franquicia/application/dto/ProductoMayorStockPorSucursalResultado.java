package com.accenture.franquicias.franquicia.application.dto;

import java.util.UUID;

/**
 * Representa un producto dentro del resultado anidado por sucursal.
 *
 * @param productoId identificador del producto
 * @param productoNombre nombre del producto
 * @param stock stock del producto
 */
public record ProductoMayorStockPorSucursalResultado(
        UUID productoId,
        String productoNombre,
        Integer stock
) {
}
