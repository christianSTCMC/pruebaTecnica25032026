package com.accenture.franquicias.producto.application.service;

/**
 * Excepcion de negocio para indicar que el producto solicitado no existe.
 */
public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException() {
        super("Producto no encontrado");
    }
}
