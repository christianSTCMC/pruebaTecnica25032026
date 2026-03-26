package com.accenture.franquicias.franquicia.application.service;

import com.accenture.franquicias.franquicia.application.dto.FranquiciaListado;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.repository.FranquiciaRepositoryJpa;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * Caso de uso para consultar el listado independiente de franquicias.
 */
@Service
public class ListarFranquiciasService {

    private final FranquiciaRepositoryJpa franquiciaRepository;

    public ListarFranquiciasService(FranquiciaRepositoryJpa franquiciaRepository) {
        this.franquiciaRepository = franquiciaRepository;
    }

    /**
     * Obtiene todas las franquicias ordenadas por nombre ascendente.
     */
    public Mono<List<FranquiciaListado>> ejecutar() {
        return Mono.fromCallable(() -> franquiciaRepository.findAllByOrderByNombreAsc()
                        .stream()
                        .map(franquicia -> new FranquiciaListado(franquicia.getId(), franquicia.getNombre()))
                        .toList())
                .subscribeOn(Schedulers.boundedElastic());
    }
}
