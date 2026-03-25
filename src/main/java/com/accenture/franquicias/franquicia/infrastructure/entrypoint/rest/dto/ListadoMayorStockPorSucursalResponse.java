package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto;

import java.util.List;
import java.util.UUID;

/**
 * Respuesta HTTP en formato de listado plano para mayor stock por sucursal.
 *
 * @param franquiciaId identificador de la franquicia consultada
 * @param franquiciaNombre nombre de la franquicia consultada
 * @param productos listado plano con el producto ganador y su sucursal
 */
public record ListadoMayorStockPorSucursalResponse(
        UUID franquiciaId,
        String franquiciaNombre,
        List<ProductoMayorStockPorSucursalListadoResponse> productos
) {
}
