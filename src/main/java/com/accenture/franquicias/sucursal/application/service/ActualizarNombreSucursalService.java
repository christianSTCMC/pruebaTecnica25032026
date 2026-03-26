package com.accenture.franquicias.sucursal.application.service;

import com.accenture.franquicias.sucursal.application.dto.SucursalCreada;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.entity.SucursalEntity;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.repository.SucursalRepositoryJpa;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

/**
 * Caso de uso para actualizar el nombre de una sucursal existente.
 */
@Service
public class ActualizarNombreSucursalService {

    private final SucursalRepositoryJpa sucursalRepository;

    public ActualizarNombreSucursalService(SucursalRepositoryJpa sucursalRepository) {
        this.sucursalRepository = sucursalRepository;
    }

    /**
     * Actualiza el nombre de la sucursal y retorna su estado final persistido.
     */
    public Mono<SucursalCreada> ejecutar(UUID sucursalId, String nombre) {
        return Mono.fromCallable(() -> {
                    SucursalEntity sucursal = sucursalRepository.findById(sucursalId)
                            .orElseThrow(SucursalNoEncontradaException::new);

                    String nombreNormalizado = nombre == null ? null : nombre.trim();
                    if (sucursalRepository.existsByNombreAndIdNot(nombreNormalizado, sucursalId)) {
                        throw new SucursalDuplicadaException();
                    }

                    try {
                        sucursal.setNombre(nombreNormalizado);
                        SucursalEntity sucursalActualizada = sucursalRepository.save(sucursal);

                        return new SucursalCreada(
                                sucursalActualizada.getId(),
                                sucursalActualizada.getNombre(),
                                sucursalActualizada.getFranquicia().getId()
                        );
                    } catch (DataIntegrityViolationException ex) {
                        // Se cubre condicion de carrera ante actualizaciones concurrentes con el mismo nombre.
                        throw new SucursalDuplicadaException();
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
