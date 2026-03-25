package com.accenture.franquicias.sucursal.application.service;

import com.accenture.franquicias.sucursal.application.dto.SucursalListado;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.repository.SucursalRepositoryJpa;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * Caso de uso para consultar el listado independiente de sucursales.
 */
@Service
public class ListarSucursalesService {

    private final SucursalRepositoryJpa sucursalRepository;

    public ListarSucursalesService(SucursalRepositoryJpa sucursalRepository) {
        this.sucursalRepository = sucursalRepository;
    }

    /**
     * Obtiene todas las sucursales ordenadas por nombre ascendente.
     */
    public Mono<List<SucursalListado>> ejecutar() {
        return Mono.fromCallable(() -> sucursalRepository.findAllByOrderByNombreAsc()
                        .stream()
                        .map(sucursal -> new SucursalListado(
                                sucursal.getId(),
                                sucursal.getNombre(),
                                sucursal.getFranquicia().getId()))
                        .toList())
                .subscribeOn(Schedulers.boundedElastic());
    }
}
