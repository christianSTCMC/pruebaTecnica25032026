package com.accenture.franquicias.franquicia.application.service;

import com.accenture.franquicias.franquicia.application.dto.ConsultaMayorStockPorSucursalResultado;
import com.accenture.franquicias.franquicia.application.dto.ProductoMayorStockPorSucursalResultado;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.entity.FranquiciaEntity;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.repository.FranquiciaRepositoryJpa;
import com.accenture.franquicias.producto.infrastructure.output.persistence.repository.ProductoMayorStockPorSucursalProjection;
import com.accenture.franquicias.producto.infrastructure.output.persistence.repository.ProductoRepositoryJpa;
import com.accenture.franquicias.sucursal.application.service.FranquiciaNoEncontradaException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso para consultar el producto con mayor stock por sucursal de una franquicia.
 */
@Service
public class ConsultarMayorStockPorSucursalService {

    private final FranquiciaRepositoryJpa franquiciaRepository;
    private final ProductoRepositoryJpa productoRepository;

    public ConsultarMayorStockPorSucursalService(
            FranquiciaRepositoryJpa franquiciaRepository,
            ProductoRepositoryJpa productoRepository) {
        this.franquiciaRepository = franquiciaRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Ejecuta la consulta agregada del contrato y valida que la franquicia exista.
     */
    public Mono<ConsultaMayorStockPorSucursalResultado> ejecutar(UUID franquiciaId) {
        return Mono.fromCallable(() -> {
                    FranquiciaEntity franquicia = franquiciaRepository.findById(franquiciaId)
                            .orElseThrow(FranquiciaNoEncontradaException::new);

                    List<ProductoMayorStockPorSucursalResultado> productos = productoRepository
                            .findProductoMayorStockPorSucursal(franquiciaId)
                            .stream()
                            .map(this::mapearProducto)
                            .toList();

                    return new ConsultaMayorStockPorSucursalResultado(
                            franquicia.getId(),
                            franquicia.getNombre(),
                            productos
                    );
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private ProductoMayorStockPorSucursalResultado mapearProducto(
            ProductoMayorStockPorSucursalProjection productoProjection) {
        return new ProductoMayorStockPorSucursalResultado(
                productoProjection.getSucursalId(),
                productoProjection.getSucursalNombre(),
                productoProjection.getProductoId(),
                productoProjection.getProductoNombre(),
                productoProjection.getStock()
        );
    }
}
