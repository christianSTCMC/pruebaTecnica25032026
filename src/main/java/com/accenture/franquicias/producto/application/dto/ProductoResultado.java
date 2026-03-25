package com.accenture.franquicias.producto.application.dto;

import java.util.UUID;

/**
 * Resultado comun para operaciones de creacion y actualizacion de producto.
 *
 * @param id identificador UUID del producto
 * @param nombre nombre final persistido del producto
 * @param stock stock actual del producto
 * @param sucursalId identificador UUID de la sucursal duenia
 */
public record ProductoResultado(
        UUID id,
        String nombre,
        Integer stock,
        UUID sucursalId
) {
}
