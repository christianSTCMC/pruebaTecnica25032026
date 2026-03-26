package com.accenture.franquicias.franquicia.application.dto;

import java.util.UUID;

/**
 * Representa una franquicia para respuestas de consulta de listados.
 *
 * @param id identificador UUID de la franquicia
 * @param nombre nombre de la franquicia
 */
public record FranquiciaListado(UUID id, String nombre) {
}
