package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest;

import com.accenture.franquicias.franquicia.application.dto.ConsultaMayorStockPorSucursalResultado;
import com.accenture.franquicias.franquicia.application.dto.FranquiciaListado;
import com.accenture.franquicias.franquicia.application.dto.ProductoMayorStockPorSucursalResultado;
import com.accenture.franquicias.franquicia.application.dto.SucursalMayorStockPorSucursalResultado;
import com.accenture.franquicias.franquicia.application.service.ConsultarMayorStockPorSucursalService;
import com.accenture.franquicias.franquicia.application.service.CrearFranquiciaService;
import com.accenture.franquicias.franquicia.application.service.ListarFranquiciasService;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.CrearFranquiciaRequest;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.FranquiciaResponse;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.MayorStockPorSucursalResponse;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.ProductoMayorStockPorSucursalResponse;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.SucursalMayorStockPorSucursalResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Controlador HTTP para operaciones de franquicia expuestas en Fase 2 y Fase 4.
 */
@RestController
@RequestMapping(path = "/api/v1/franquicias", produces = MediaType.APPLICATION_JSON_VALUE)
public class FranquiciaController {

    private final CrearFranquiciaService crearFranquiciaService;
    private final ListarFranquiciasService listarFranquiciasService;
    private final ConsultarMayorStockPorSucursalService consultarMayorStockPorSucursalService;

    public FranquiciaController(
            CrearFranquiciaService crearFranquiciaService,
            ListarFranquiciasService listarFranquiciasService,
            ConsultarMayorStockPorSucursalService consultarMayorStockPorSucursalService) {
        this.crearFranquiciaService = crearFranquiciaService;
        this.listarFranquiciasService = listarFranquiciasService;
        this.consultarMayorStockPorSucursalService = consultarMayorStockPorSucursalService;
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

    /**
     * Lista todas las franquicias de forma independiente.
     */
    @GetMapping
    public Mono<ResponseEntity<List<FranquiciaResponse>>> listarFranquicias() {
        return listarFranquiciasService.ejecutar()
                .map(this::mapearFranquicias)
                .map(ResponseEntity::ok);
    }

    /**
     * Obtiene el producto con mayor stock por sucursal para la franquicia indicada.
     */
    @GetMapping("/{franquiciaId}/productos/mayor-stock-por-sucursal")
    public Mono<ResponseEntity<MayorStockPorSucursalResponse>> consultarMayorStockPorSucursal(
            @PathVariable UUID franquiciaId) {
        return consultarMayorStockPorSucursalService.ejecutar(franquiciaId)
                .map(this::mapearRespuestaMayorStock)
                .map(ResponseEntity::ok);
    }

    private MayorStockPorSucursalResponse mapearRespuestaMayorStock(
            ConsultaMayorStockPorSucursalResultado resultado) {
        return new MayorStockPorSucursalResponse(
                resultado.franquiciaId(),
                resultado.franquiciaNombre(),
                resultado.sucursales().stream()
                        .map(this::mapearSucursalMayorStock)
                        .toList()
        );
    }

    private SucursalMayorStockPorSucursalResponse mapearSucursalMayorStock(
            SucursalMayorStockPorSucursalResultado sucursalResultado) {
        return new SucursalMayorStockPorSucursalResponse(
                sucursalResultado.sucursalId(),
                sucursalResultado.sucursalNombre(),
                sucursalResultado.productos().stream()
                        .map(this::mapearProductoMayorStock)
                        .toList()
        );
    }

    private ProductoMayorStockPorSucursalResponse mapearProductoMayorStock(
            ProductoMayorStockPorSucursalResultado productoResultado) {
        return new ProductoMayorStockPorSucursalResponse(
                productoResultado.productoId(),
                productoResultado.productoNombre(),
                productoResultado.stock()
        );
    }

    private List<FranquiciaResponse> mapearFranquicias(List<FranquiciaListado> franquicias) {
        return franquicias.stream()
                .map(franquicia -> new FranquiciaResponse(franquicia.id(), franquicia.nombre()))
                .toList();
    }
}
