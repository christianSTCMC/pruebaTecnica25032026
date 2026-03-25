package com.accenture.franquicias.producto.application.service;

/**
 * Excepcion de negocio para indicar un conflicto de nombre de producto.
 */
public class ProductoDuplicadoEnSucursalException extends RuntimeException {

    public ProductoDuplicadoEnSucursalException() {
        super("Ya existe un producto con ese nombre en la sucursal");
    }
}
