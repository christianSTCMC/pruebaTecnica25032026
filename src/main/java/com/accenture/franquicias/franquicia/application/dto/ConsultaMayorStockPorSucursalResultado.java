package com.accenture.franquicias.franquicia.application.dto;

import java.util.List;
import java.util.UUID;

/**
 * Resultado agregado de la consulta de mayor stock por sucursal de una franquicia.
 *
 * @param franquiciaId identificador de la franquicia consultada
 * @param franquiciaNombre nombre de la franquicia consultada
 * @param sucursales listado de sucursales con sus productos para mantener jerarquia
 *                  franquicia -> sucursal -> producto
 */
public record ConsultaMayorStockPorSucursalResultado(
        UUID franquiciaId,
        String franquiciaNombre,
        List<SucursalMayorStockPorSucursalResultado> sucursales
) {
}
