package com.accenture.franquicias.sucursal.application.service;

/**
 * Excepcion de negocio para indicar que la franquicia solicitada no existe.
 */
public class FranquiciaNoEncontradaException extends RuntimeException {

    public FranquiciaNoEncontradaException() {
        super("Franquicia no encontrada");
    }
}
