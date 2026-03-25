package com.accenture.franquicias.franquicia.application.service;

import com.accenture.franquicias.franquicia.application.dto.FranquiciaCreada;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.entity.FranquiciaEntity;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.repository.FranquiciaRepositoryJpa;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Caso de uso para crear una franquicia respetando el contrato publico del API.
 *
 * <p>Como la persistencia JPA es bloqueante, la ejecucion se delega al
 * scheduler boundedElastic para no bloquear el event loop de WebFlux.</p>
 */
@Service
public class CrearFranquiciaService {

    private final FranquiciaRepositoryJpa franquiciaRepository;

    public CrearFranquiciaService(FranquiciaRepositoryJpa franquiciaRepository) {
        this.franquiciaRepository = franquiciaRepository;
    }

    /**
     * Crea una nueva franquicia con nombre obligatorio.
     */
    public Mono<FranquiciaCreada> ejecutar(String nombre) {
        return Mono.fromCallable(() -> {
                    FranquiciaEntity franquiciaGuardada = franquiciaRepository.save(new FranquiciaEntity(nombre));
                    return new FranquiciaCreada(franquiciaGuardada.getId(), franquiciaGuardada.getNombre());
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
