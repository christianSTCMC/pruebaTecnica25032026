package com.accenture.franquicias.producto.infrastructure.output.persistence.repository;

import com.accenture.franquicias.producto.infrastructure.output.persistence.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de persistencia para reglas de productos y consultas del contrato.
 */
public interface ProductoRepositoryJpa extends JpaRepository<ProductoEntity, UUID> {

    /**
     * Retorna todos los productos ordenados por nombre para consultas independientes.
     */
    List<ProductoEntity> findAllByOrderByNombreAsc();

    /**
     * Permite validar unicidad de nombre de producto dentro de una sucursal.
     */
    boolean existsBySucursal_IdAndNombre(UUID sucursalId, String nombre);

    /**
     * Valida duplicidad de nombre dentro de la sucursal excluyendo el producto actual.
     */
    boolean existsBySucursal_IdAndNombreAndIdNot(UUID sucursalId, String nombre, UUID productoId);

    /**
     * Se usa para validar pertenencia del producto a la sucursal durante eliminacion.
     */
    boolean existsByIdAndSucursal_Id(UUID productoId, UUID sucursalId);

    /**
     * Consulta el producto con mayor stock de una sucursal,
     * desempata por nombre ascendente para mantener respuesta determinista.
     */
    Optional<ProductoEntity> findFirstBySucursal_IdOrderByStockDescNombreAsc(UUID sucursalId);

    /**
     * Consulta para soportar el endpoint de mayor stock por sucursal de una franquicia.
     * Devuelve un producto por sucursal aplicando desempate alfabetico por nombre.
     */
    @Query("""
            select
                p.sucursal.id as sucursalId,
                p.sucursal.nombre as sucursalNombre,
                p.id as productoId,
                p.nombre as productoNombre,
                p.stock as stock
            from ProductoEntity p
            where p.sucursal.franquicia.id = :franquiciaId
              and p.stock = (
                    select max(p2.stock)
                    from ProductoEntity p2
                    where p2.sucursal.id = p.sucursal.id
              )
              and p.nombre = (
                    select min(p3.nombre)
                    from ProductoEntity p3
                    where p3.sucursal.id = p.sucursal.id
                      and p3.stock = p.stock
              )
            order by p.sucursal.nombre asc
            """)
    List<ProductoMayorStockPorSucursalProjection> findProductoMayorStockPorSucursal(@Param("franquiciaId") UUID franquiciaId);
}
