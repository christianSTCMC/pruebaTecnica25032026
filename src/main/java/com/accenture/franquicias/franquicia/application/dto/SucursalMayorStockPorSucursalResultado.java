package com.accenture.franquicias.franquicia.application.dto;

import java.util.List;
import java.util.UUID;

/**
 * Representa una sucursal con su listado de productos en el resultado de consulta.
 *
 * @param sucursalId identificador de la sucursal
 * @param sucursalNombre nombre de la sucursal
 * @param productos listado de productos de la sucursal para el contrato de salida
 */
public record SucursalMayorStockPorSucursalResultado(
        UUID sucursalId,
        String sucursalNombre,
        List<ProductoMayorStockPorSucursalResultado> productos
) {
}
