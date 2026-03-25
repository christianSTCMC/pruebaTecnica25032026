package com.accenture.franquicias.producto.application.service;

import com.accenture.franquicias.producto.application.dto.ProductoResultado;
import com.accenture.franquicias.producto.infrastructure.output.persistence.entity.ProductoEntity;
import com.accenture.franquicias.producto.infrastructure.output.persistence.repository.ProductoRepositoryJpa;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

/**
 * Caso de uso para actualizar el nombre de un producto existente.
 */
@Service
public class ActualizarNombreProductoService {

    private final ProductoRepositoryJpa productoRepository;

    public ActualizarNombreProductoService(ProductoRepositoryJpa productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Actualiza el nombre validando que no se duplique dentro de la misma sucursal.
     */
    public Mono<ProductoResultado> ejecutar(UUID productoId, String nombre) {
        return Mono.fromCallable(() -> {
                    ProductoEntity producto = productoRepository.findById(productoId)
                            .orElseThrow(ProductoNoEncontradoException::new);

                    String nombreNormalizado = nombre == null ? null : nombre.trim();
                    UUID sucursalId = producto.getSucursal().getId();

                    if (productoRepository.existsBySucursal_IdAndNombreAndIdNot(
                            sucursalId,
                            nombreNormalizado,
                            productoId)
                    ) {
                        throw new ProductoDuplicadoEnSucursalException();
                    }

                    try {
                        producto.setNombre(nombreNormalizado);
                        ProductoEntity productoActualizado = productoRepository.save(producto);

                        return new ProductoResultado(
                                productoActualizado.getId(),
                                productoActualizado.getNombre(),
                                productoActualizado.getStock(),
                                sucursalId
                        );
                    } catch (DataIntegrityViolationException ex) {
                        // Se cubre condicion de carrera para evitar nombres duplicados por sucursal.
                        throw new ProductoDuplicadoEnSucursalException();
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
