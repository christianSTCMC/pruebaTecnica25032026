package com.accenture.franquicias.producto.application.service;

import com.accenture.franquicias.producto.infrastructure.output.persistence.entity.ProductoEntity;
import com.accenture.franquicias.producto.infrastructure.output.persistence.repository.ProductoRepositoryJpa;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.repository.SucursalRepositoryJpa;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

/**
 * Caso de uso para eliminar productos de una sucursal.
 */
@Service
public class EliminarProductoService {

    private final SucursalRepositoryJpa sucursalRepository;
    private final ProductoRepositoryJpa productoRepository;

    public EliminarProductoService(
            SucursalRepositoryJpa sucursalRepository,
            ProductoRepositoryJpa productoRepository) {
        this.sucursalRepository = sucursalRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Elimina un producto validando pertenencia a la sucursal indicada.
     */
    public Mono<Void> ejecutar(UUID sucursalId, UUID productoId) {
        return Mono.fromRunnable(() -> {
                    if (!sucursalRepository.existsById(sucursalId)) {
                        throw new SucursalNoEncontradaException();
                    }

                    ProductoEntity producto = productoRepository.findById(productoId)
                            .orElseThrow(ProductoNoEncontradoException::new);

                    if (!producto.getSucursal().getId().equals(sucursalId)) {
                        throw new ProductoNoPerteneceASucursalException();
                    }

                    productoRepository.delete(producto);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
