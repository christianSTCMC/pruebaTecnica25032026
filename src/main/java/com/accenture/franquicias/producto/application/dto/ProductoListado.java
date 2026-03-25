package com.accenture.franquicias.producto.application.dto;

import java.util.UUID;

/**
 * Representa un producto para respuestas de consulta de listados.
 *
 * @param id identificador UUID del producto
 * @param nombre nombre del producto
 * @param stock stock actual del producto
 * @param sucursalId identificador UUID de la sucursal asociada
 */
public record ProductoListado(UUID id, String nombre, Integer stock, UUID sucursalId) {
}
