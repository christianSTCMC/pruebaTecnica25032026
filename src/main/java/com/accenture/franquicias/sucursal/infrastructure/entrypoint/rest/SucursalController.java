package com.accenture.franquicias.sucursal.infrastructure.entrypoint.rest;

import com.accenture.franquicias.sucursal.application.dto.SucursalListado;
import com.accenture.franquicias.sucursal.application.service.CrearSucursalService;
import com.accenture.franquicias.sucursal.application.service.ListarSucursalesService;
import com.accenture.franquicias.sucursal.infrastructure.entrypoint.rest.dto.CrearSucursalRequest;
import com.accenture.franquicias.sucursal.infrastructure.entrypoint.rest.dto.SucursalResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Controlador HTTP para operaciones de sucursal expuestas en la Fase 2.
 */
@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class SucursalController {

    private final CrearSucursalService crearSucursalService;
    private final ListarSucursalesService listarSucursalesService;

    public SucursalController(
            CrearSucursalService crearSucursalService,
            ListarSucursalesService listarSucursalesService) {
        this.crearSucursalService = crearSucursalService;
        this.listarSucursalesService = listarSucursalesService;
    }

    /**
     * Endpoint para crear una sucursal dentro de una franquicia existente.
     */
    @PostMapping(path = "/franquicias/{franquiciaId}/sucursales", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<SucursalResponse>> crearSucursal(
            @PathVariable UUID franquiciaId,
            @Valid @RequestBody CrearSucursalRequest request) {
        return crearSucursalService.ejecutar(franquiciaId, request.nombre())
                .map(resultado -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(new SucursalResponse(resultado.id(), resultado.nombre(), resultado.franquiciaId())));
    }

    /**
     * Lista todas las sucursales de forma independiente.
     */
    @GetMapping(path = "/sucursales")
    public Mono<ResponseEntity<List<SucursalResponse>>> listarSucursales() {
        return listarSucursalesService.ejecutar()
                .map(this::mapearSucursales)
                .map(ResponseEntity::ok);
    }

    private List<SucursalResponse> mapearSucursales(List<SucursalListado> sucursales) {
        return sucursales.stream()
                .map(sucursal -> new SucursalResponse(sucursal.id(), sucursal.nombre(), sucursal.franquiciaId()))
                .toList();
    }
}
