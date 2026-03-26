package com.accenture.franquicias.franquicia.application.service;

import com.accenture.franquicias.franquicia.application.dto.FranquiciaCreada;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.entity.FranquiciaEntity;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.repository.FranquiciaRepositoryJpa;
import com.accenture.franquicias.sucursal.application.service.FranquiciaNoEncontradaException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

/**
 * Caso de uso para actualizar el nombre de una franquicia existente.
 */
@Service
public class ActualizarNombreFranquiciaService {

    private final FranquiciaRepositoryJpa franquiciaRepository;

    public ActualizarNombreFranquiciaService(FranquiciaRepositoryJpa franquiciaRepository) {
        this.franquiciaRepository = franquiciaRepository;
    }

    /**
     * Actualiza el nombre de la franquicia y retorna su estado final persistido.
     */
    public Mono<FranquiciaCreada> ejecutar(UUID franquiciaId, String nombre) {
        return Mono.fromCallable(() -> {
                    FranquiciaEntity franquicia = franquiciaRepository.findById(franquiciaId)
                            .orElseThrow(FranquiciaNoEncontradaException::new);

                    String nombreNormalizado = nombre == null ? null : nombre.trim();
                    if (franquiciaRepository.existsByNombreAndIdNot(nombreNormalizado, franquiciaId)) {
                        throw new FranquiciaDuplicadaException();
                    }

                    try {
                        franquicia.setNombre(nombreNormalizado);
                        FranquiciaEntity franquiciaActualizada = franquiciaRepository.save(franquicia);

                        return new FranquiciaCreada(
                                franquiciaActualizada.getId(),
                                franquiciaActualizada.getNombre()
                        );
                    } catch (DataIntegrityViolationException ex) {
                        // Se cubre condicion de carrera ante actualizaciones concurrentes con el mismo nombre.
                        throw new FranquiciaDuplicadaException();
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
