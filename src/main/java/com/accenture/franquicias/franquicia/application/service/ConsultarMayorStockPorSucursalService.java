package com.accenture.franquicias.franquicia.application.service;

import com.accenture.franquicias.franquicia.application.dto.ConsultaMayorStockPorSucursalResultado;
import com.accenture.franquicias.franquicia.application.dto.ProductoMayorStockPorSucursalResultado;
import com.accenture.franquicias.franquicia.application.dto.SucursalMayorStockPorSucursalResultado;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.entity.FranquiciaEntity;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.repository.FranquiciaRepositoryJpa;
import com.accenture.franquicias.producto.infrastructure.output.persistence.repository.ProductoMayorStockPorSucursalProjection;
import com.accenture.franquicias.producto.infrastructure.output.persistence.repository.ProductoRepositoryJpa;
import com.accenture.franquicias.sucursal.application.service.FranquiciaNoEncontradaException;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.entity.SucursalEntity;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.repository.SucursalRepositoryJpa;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Caso de uso para consultar el producto con mayor stock por sucursal de una franquicia.
 */
@Service
public class ConsultarMayorStockPorSucursalService {

    private final FranquiciaRepositoryJpa franquiciaRepository;
    private final SucursalRepositoryJpa sucursalRepository;
    private final ProductoRepositoryJpa productoRepository;

    public ConsultarMayorStockPorSucursalService(
            FranquiciaRepositoryJpa franquiciaRepository,
            SucursalRepositoryJpa sucursalRepository,
            ProductoRepositoryJpa productoRepository) {
        this.franquiciaRepository = franquiciaRepository;
        this.sucursalRepository = sucursalRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Ejecuta la consulta agregada del contrato y valida que la franquicia exista.
     */
    public Mono<ConsultaMayorStockPorSucursalResultado> ejecutar(UUID franquiciaId) {
        return Mono.fromCallable(() -> {
                    FranquiciaEntity franquicia = franquiciaRepository.findById(franquiciaId)
                            .orElseThrow(FranquiciaNoEncontradaException::new);

                    Map<UUID, ProductoMayorStockPorSucursalProjection> productoGanadorPorSucursal = productoRepository
                            .findProductoMayorStockPorSucursal(franquiciaId)
                            .stream()
                            .collect(Collectors.toMap(
                                    ProductoMayorStockPorSucursalProjection::getSucursalId,
                                    Function.identity(),
                                    (primero, segundo) -> primero
                            ));

                    List<SucursalMayorStockPorSucursalResultado> sucursales = sucursalRepository
                            .findByFranquicia_IdOrderByNombreAsc(franquiciaId)
                            .stream()
                            .map(sucursal -> mapearSucursal(
                                    sucursal,
                                    productoGanadorPorSucursal.get(sucursal.getId())
                            ))
                            .toList();

                    return new ConsultaMayorStockPorSucursalResultado(
                            franquicia.getId(),
                            franquicia.getNombre(),
                            sucursales
                    );
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private SucursalMayorStockPorSucursalResultado mapearSucursal(
            SucursalEntity sucursal,
            ProductoMayorStockPorSucursalProjection productoProjection) {
        // Si una sucursal no tiene productos, se mantiene en la respuesta con lista vacia.
        List<ProductoMayorStockPorSucursalResultado> productos = productoProjection == null
                ? List.of()
                : List.of(mapearProducto(productoProjection));

        return new SucursalMayorStockPorSucursalResultado(
                sucursal.getId(),
                sucursal.getNombre(),
                productos
        );
    }

    private ProductoMayorStockPorSucursalResultado mapearProducto(
            ProductoMayorStockPorSucursalProjection productoProjection) {
        return new ProductoMayorStockPorSucursalResultado(
                productoProjection.getProductoId(),
                productoProjection.getProductoNombre(),
                productoProjection.getStock()
        );
    }
}
