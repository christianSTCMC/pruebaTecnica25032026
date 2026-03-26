package com.accenture.franquicias.producto.application.service;

/**
 * Excepcion de negocio para rechazar eliminacion fuera de la sucursal duenia.
 */
public class ProductoNoPerteneceASucursalException extends RuntimeException {

    public ProductoNoPerteneceASucursalException() {
        super("El producto no pertenece a la sucursal indicada");
    }
}
