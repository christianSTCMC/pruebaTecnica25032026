package com.accenture.franquicias.producto.infrastructure.output.persistence.repository;

import java.util.UUID;

/**
 * Proyeccion de solo lectura para la consulta agregada de mayor stock por sucursal.
 *
 * <p>Se usa para evitar cargar entidades completas y para mapear de forma directa
 * la respuesta del caso de uso de Fase 4.</p>
 */
public interface ProductoMayorStockPorSucursalProjection {

    /**
     * Identificador de la sucursal donde se encontro el producto ganador.
     */
    UUID getSucursalId();

    /**
     * Nombre de la sucursal para la respuesta del contrato.
     */
    String getSucursalNombre();

    /**
     * Identificador del producto con mayor stock.
     */
    UUID getProductoId();

    /**
     * Nombre del producto ganador tras aplicar desempate.
     */
    String getProductoNombre();

    /**
     * Stock final del producto seleccionado.
     */
    Integer getStock();
}
