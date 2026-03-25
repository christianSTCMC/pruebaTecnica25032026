package com.accenture.franquicias.producto.application.service;

import com.accenture.franquicias.producto.application.dto.ProductoResultado;
import com.accenture.franquicias.producto.infrastructure.output.persistence.entity.ProductoEntity;
import com.accenture.franquicias.producto.infrastructure.output.persistence.repository.ProductoRepositoryJpa;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

/**
 * Caso de uso para actualizar el stock de un producto existente.
 */
@Service
public class ActualizarStockProductoService {

    private final ProductoRepositoryJpa productoRepository;

    public ActualizarStockProductoService(ProductoRepositoryJpa productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Actualiza el stock y retorna el estado final persistido.
     */
    public Mono<ProductoResultado> ejecutar(UUID productoId, Integer stock) {
        return Mono.fromCallable(() -> {
                    ProductoEntity producto = productoRepository.findById(productoId)
                            .orElseThrow(ProductoNoEncontradoException::new);

                    producto.setStock(stock);
                    ProductoEntity productoActualizado = productoRepository.save(producto);

                    return new ProductoResultado(
                            productoActualizado.getId(),
                            productoActualizado.getNombre(),
                            productoActualizado.getStock(),
                            productoActualizado.getSucursal().getId()
                    );
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
