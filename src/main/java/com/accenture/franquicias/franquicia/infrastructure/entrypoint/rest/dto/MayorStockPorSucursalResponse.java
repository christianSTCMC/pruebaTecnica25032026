package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto;

import java.util.List;
import java.util.UUID;

/**
 * Respuesta HTTP para la consulta de mayor stock por sucursal.
 *
 * @param franquiciaId identificador de la franquicia consultada
 * @param franquiciaNombre nombre de la franquicia consultada
 * @param productos producto ganador por cada sucursal con productos
 */
public record MayorStockPorSucursalResponse(
        UUID franquiciaId,
        String franquiciaNombre,
        List<ProductoMayorStockPorSucursalResponse> productos
) {
}
