package com.accenture.franquicias.producto.application.service;

/**
 * Excepcion de negocio para indicar que la sucursal objetivo no existe.
 */
public class SucursalNoEncontradaException extends RuntimeException {

    public SucursalNoEncontradaException() {
        super("Sucursal no encontrada");
    }
}
