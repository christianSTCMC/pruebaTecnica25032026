package com.accenture.franquicias.producto.infrastructure.entrypoint.rest.dto;

import java.util.UUID;

/**
 * Response de producto para operaciones de creacion y actualizacion de stock.
 *
 * @param id identificador UUID del producto
 * @param nombre nombre del producto
 * @param stock stock actual del producto
 * @param sucursalId identificador UUID de la sucursal asociada
 */
public record ProductoResponse(UUID id, String nombre, Integer stock, UUID sucursalId) {
}
