package com.accenture.franquicias.sucursal.infrastructure.entrypoint.rest.dto;

import java.util.UUID;

/**
 * Response de creacion de sucursal, alineado al contrato publico.
 *
 * @param id identificador UUID de la sucursal
 * @param nombre nombre de la sucursal
 * @param franquiciaId identificador UUID de la franquicia asociada
 */
public record SucursalResponse(UUID id, String nombre, UUID franquiciaId) {
}
