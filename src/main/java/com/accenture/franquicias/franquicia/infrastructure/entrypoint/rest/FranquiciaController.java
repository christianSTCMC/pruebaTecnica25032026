package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest;

import com.accenture.franquicias.franquicia.application.service.CrearFranquiciaService;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.CrearFranquiciaRequest;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.FranquiciaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Controlador HTTP para operaciones de franquicia expuestas en la Fase 2.
 */
@RestController
@RequestMapping(path = "/api/v1/franquicias", produces = MediaType.APPLICATION_JSON_VALUE)
public class FranquiciaController {

    private final CrearFranquiciaService crearFranquiciaService;

    public FranquiciaController(CrearFranquiciaService crearFranquiciaService) {
        this.crearFranquiciaService = crearFranquiciaService;
    }

    /**
     * Endpoint para crear una nueva franquicia.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<FranquiciaResponse>> crearFranquicia(
            @Valid @RequestBody CrearFranquiciaRequest request) {
        return crearFranquiciaService.ejecutar(request.nombre())
                .map(resultado -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(new FranquiciaResponse(resultado.id(), resultado.nombre())));
    }
}
