package com.accenture.franquicias.sucursal.application.service;

import com.accenture.franquicias.franquicia.infrastructure.output.persistence.entity.FranquiciaEntity;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.repository.FranquiciaRepositoryJpa;
import com.accenture.franquicias.sucursal.application.dto.SucursalCreada;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.entity.SucursalEntity;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.repository.SucursalRepositoryJpa;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

/**
 * Caso de uso para crear sucursales dentro de una franquicia existente.
 *
 * <p>La operacion bloqueante de JPA se encapsula en boundedElastic para
 * conservar el comportamiento no bloqueante de la capa WebFlux.</p>
 */
@Service
public class CrearSucursalService {

    private final FranquiciaRepositoryJpa franquiciaRepository;
    private final SucursalRepositoryJpa sucursalRepository;

    public CrearSucursalService(
            FranquiciaRepositoryJpa franquiciaRepository,
            SucursalRepositoryJpa sucursalRepository) {
        this.franquiciaRepository = franquiciaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    /**
     * Crea una sucursal para la franquicia indicada en el path.
     */
    public Mono<SucursalCreada> ejecutar(UUID franquiciaId, String nombre) {
        return Mono.fromCallable(() -> {
                    FranquiciaEntity franquicia = franquiciaRepository.findById(franquiciaId)
                            .orElseThrow(FranquiciaNoEncontradaException::new);

                    String nombreNormalizado = nombre == null ? null : nombre.trim();
                    if (sucursalRepository.existsByNombre(nombreNormalizado)) {
                        throw new SucursalDuplicadaException();
                    }

                    try {
                        SucursalEntity sucursalGuardada = sucursalRepository.save(
                                new SucursalEntity(nombreNormalizado, franquicia)
                        );
                        return new SucursalCreada(
                                sucursalGuardada.getId(),
                                sucursalGuardada.getNombre(),
                                franquiciaId
                        );
                    } catch (DataIntegrityViolationException ex) {
                        // Se cubre condicion de carrera ante creaciones concurrentes con el mismo nombre.
                        throw new SucursalDuplicadaException();
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
