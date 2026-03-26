package com.accenture.franquicias.producto.application.service;

import com.accenture.franquicias.producto.application.dto.ProductoResultado;
import com.accenture.franquicias.producto.infrastructure.output.persistence.entity.ProductoEntity;
import com.accenture.franquicias.producto.infrastructure.output.persistence.repository.ProductoRepositoryJpa;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.entity.SucursalEntity;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.repository.SucursalRepositoryJpa;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

/**
 * Caso de uso para crear productos dentro de una sucursal existente.
 */
@Service
public class CrearProductoService {

    private final SucursalRepositoryJpa sucursalRepository;
    private final ProductoRepositoryJpa productoRepository;

    public CrearProductoService(
            SucursalRepositoryJpa sucursalRepository,
            ProductoRepositoryJpa productoRepository) {
        this.sucursalRepository = sucursalRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Crea un producto validando existencia de sucursal y unicidad por nombre.
     */
    public Mono<ProductoResultado> ejecutar(UUID sucursalId, String nombre, Integer stock) {
        return Mono.fromCallable(() -> {
                    SucursalEntity sucursal = sucursalRepository.findById(sucursalId)
                            .orElseThrow(SucursalNoEncontradaException::new);

                    String nombreNormalizado = nombre == null ? null : nombre.trim();
                    if (productoRepository.existsBySucursal_IdAndNombre(sucursalId, nombreNormalizado)) {
                        throw new ProductoDuplicadoEnSucursalException();
                    }

                    try {
                        ProductoEntity productoGuardado = productoRepository.save(
                                new ProductoEntity(nombreNormalizado, stock, sucursal)
                        );

                        return new ProductoResultado(
                                productoGuardado.getId(),
                                productoGuardado.getNombre(),
                                productoGuardado.getStock(),
                                sucursalId
                        );
                    } catch (DataIntegrityViolationException ex) {
                        // Se cubre la condicion de carrera donde otro request crea el mismo nombre primero.
                        throw new ProductoDuplicadoEnSucursalException();
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
