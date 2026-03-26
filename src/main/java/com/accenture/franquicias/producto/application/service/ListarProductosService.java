package com.accenture.franquicias.producto.application.service;

import com.accenture.franquicias.producto.application.dto.ProductoListado;
import com.accenture.franquicias.producto.infrastructure.output.persistence.repository.ProductoRepositoryJpa;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * Caso de uso para consultar el listado independiente de productos.
 */
@Service
public class ListarProductosService {

    private final ProductoRepositoryJpa productoRepository;

    public ListarProductosService(ProductoRepositoryJpa productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Obtiene todos los productos ordenados por nombre ascendente.
     */
    public Mono<List<ProductoListado>> ejecutar() {
        return Mono.fromCallable(() -> productoRepository.findAllByOrderByNombreAsc()
                        .stream()
                        .map(producto -> new ProductoListado(
                                producto.getId(),
                                producto.getNombre(),
                                producto.getStock(),
                                producto.getSucursal().getId()))
                        .toList())
                .subscribeOn(Schedulers.boundedElastic());
    }
}
