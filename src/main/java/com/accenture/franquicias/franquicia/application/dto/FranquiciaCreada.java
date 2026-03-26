package com.accenture.franquicias.franquicia.application.dto;

import java.util.UUID;

/**
 * Representa el resultado del caso de uso de creacion de franquicia.
 *
 * @param id identificador unico de la franquicia creada
 * @param nombre nombre final persistido para la franquicia
 */
public record FranquiciaCreada(UUID id, String nombre) {
}
