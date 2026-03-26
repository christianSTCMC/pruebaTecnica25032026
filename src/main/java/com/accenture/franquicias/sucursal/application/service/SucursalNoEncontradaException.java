package com.accenture.franquicias.sucursal.application.service;

/**
 * Excepcion de negocio para indicar que la sucursal solicitada no existe.
 */
public class SucursalNoEncontradaException extends RuntimeException {

    public SucursalNoEncontradaException() {
        super("Sucursal no encontrada");
    }
}
