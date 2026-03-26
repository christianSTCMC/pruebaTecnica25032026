package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto;

import java.util.UUID;

/**
 * Response de creacion de franquicia, alineado al contrato publico.
 *
 * @param id identificador UUID de la franquicia
 * @param nombre nombre de la franquicia
 */
public record FranquiciaResponse(UUID id, String nombre) {
}
