package com.accenture.franquicias.sucursal.application.dto;

import java.util.UUID;

/**
 * Representa el resultado del caso de uso de creacion de sucursal.
 *
 * @param id identificador UUID de la sucursal creada
 * @param nombre nombre final de la sucursal
 * @param franquiciaId identificador UUID de la franquicia duenia
 */
public record SucursalCreada(UUID id, String nombre, UUID franquiciaId) {
}
