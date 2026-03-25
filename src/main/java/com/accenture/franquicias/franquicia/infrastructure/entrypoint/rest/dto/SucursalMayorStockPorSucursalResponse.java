package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto;

import java.util.List;
import java.util.UUID;

/**
 * Item de respuesta con datos de sucursal y sus productos.
 *
 * @param sucursalId identificador de la sucursal
 * @param sucursalNombre nombre de la sucursal
 * @param productos listado de productos de la sucursal
 */
public record SucursalMayorStockPorSucursalResponse(
        UUID sucursalId,
        String sucursalNombre,
        List<ProductoMayorStockPorSucursalResponse> productos
) {
}
