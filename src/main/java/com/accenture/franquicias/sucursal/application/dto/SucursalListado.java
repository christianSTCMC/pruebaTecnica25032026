package com.accenture.franquicias.sucursal.application.dto;

import java.util.UUID;

/**
 * Representa una sucursal para respuestas de consulta de listados.
 *
 * @param id identificador UUID de la sucursal
 * @param nombre nombre de la sucursal
 * @param franquiciaId identificador UUID de la franquicia asociada
 */
public record SucursalListado(UUID id, String nombre, UUID franquiciaId) {
}
