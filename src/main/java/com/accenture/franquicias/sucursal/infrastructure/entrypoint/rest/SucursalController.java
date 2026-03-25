package com.accenture.franquicias.sucursal.infrastructure.entrypoint.rest;

import com.accenture.franquicias.sucursal.application.service.CrearSucursalService;
import com.accenture.franquicias.sucursal.infrastructure.entrypoint.rest.dto.CrearSucursalRequest;
import com.accenture.franquicias.sucursal.infrastructure.entrypoint.rest.dto.SucursalResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Controlador HTTP para operaciones de sucursal expuestas en la Fase 2.
 */
@RestController
@RequestMapping(path = "/api/v1/franquicias", produces = MediaType.APPLICATION_JSON_VALUE)
public class SucursalController {

    private final CrearSucursalService crearSucursalService;

    public SucursalController(CrearSucursalService crearSucursalService) {
        this.crearSucursalService = crearSucursalService;
    }

    /**
     * Endpoint para crear una sucursal dentro de una franquicia existente.
     */
    @PostMapping(path = "/{franquiciaId}/sucursales", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<SucursalResponse>> crearSucursal(
            @PathVariable UUID franquiciaId,
            @Valid @RequestBody CrearSucursalRequest request) {
        return crearSucursalService.ejecutar(franquiciaId, request.nombre())
                .map(resultado -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(new SucursalResponse(resultado.id(), resultado.nombre(), resultado.franquiciaId())));
    }
}
