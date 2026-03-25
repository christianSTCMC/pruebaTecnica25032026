package com.accenture.franquicias.franquicia.application.service;

/**
 * Excepcion de negocio para indicar conflicto por nombre de franquicia duplicado.
 */
public class FranquiciaDuplicadaException extends RuntimeException {

    public FranquiciaDuplicadaException() {
        super("Ya existe una franquicia con ese nombre");
    }
}
