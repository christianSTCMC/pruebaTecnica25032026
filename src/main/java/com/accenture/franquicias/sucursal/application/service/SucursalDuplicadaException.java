package com.accenture.franquicias.sucursal.application.service;

/**
 * Excepcion de negocio para indicar conflicto por nombre de sucursal duplicado.
 */
public class SucursalDuplicadaException extends RuntimeException {

    public SucursalDuplicadaException() {
        super("Ya existe una sucursal con ese nombre");
    }
}
