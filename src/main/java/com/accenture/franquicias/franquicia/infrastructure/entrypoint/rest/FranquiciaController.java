package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest;

import com.accenture.franquicias.compartido.infrastructure.entrypoint.rest.error.ApiErrorResponse;
import com.accenture.franquicias.franquicia.application.dto.ConsultaMayorStockPorSucursalResultado;
import com.accenture.franquicias.franquicia.application.dto.FranquiciaListado;
import com.accenture.franquicias.franquicia.application.dto.ProductoMayorStockPorSucursalResultado;
import com.accenture.franquicias.franquicia.application.dto.SucursalMayorStockPorSucursalResultado;
import com.accenture.franquicias.franquicia.application.service.ActualizarNombreFranquiciaService;
import com.accenture.franquicias.franquicia.application.service.ConsultarMayorStockPorSucursalService;
import com.accenture.franquicias.franquicia.application.service.CrearFranquiciaService;
import com.accenture.franquicias.franquicia.application.service.ListarFranquiciasService;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.ActualizarNombreFranquiciaRequest;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.CrearFranquiciaRequest;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.FranquiciaResponse;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.ListadoMayorStockPorSucursalResponse;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.MayorStockPorSucursalResponse;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.ProductoMayorStockPorSucursalListadoResponse;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.ProductoMayorStockPorSucursalResponse;
import com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest.dto.SucursalMayorStockPorSucursalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@Tag(name = "Franquicias", description = "Operaciones para gestionar franquicias y sus consultas agregadas")
public class FranquiciaController {

    private final ActualizarNombreFranquiciaService actualizarNombreFranquiciaService;
    private final CrearFranquiciaService crearFranquiciaService;
    private final ListarFranquiciasService listarFranquiciasService;
    private final ConsultarMayorStockPorSucursalService consultarMayorStockPorSucursalService;

    public FranquiciaController(
            ActualizarNombreFranquiciaService actualizarNombreFranquiciaService,
            CrearFranquiciaService crearFranquiciaService,
            ListarFranquiciasService listarFranquiciasService,
            ConsultarMayorStockPorSucursalService consultarMayorStockPorSucursalService) {
        this.actualizarNombreFranquiciaService = actualizarNombreFranquiciaService;
        this.crearFranquiciaService = crearFranquiciaService;
        this.listarFranquiciasService = listarFranquiciasService;
        this.consultarMayorStockPorSucursalService = consultarMayorStockPorSucursalService;
    }

    /**
     * Endpoint para crear una nueva franquicia.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Crear franquicia",
            description = "Crea una franquicia validando que el nombre no sea nulo ni vacio."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Franquicia creada",
                    content = @Content(schema = @Schema(implementation = FranquiciaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto por nombre de franquicia duplicado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public Mono<ResponseEntity<FranquiciaResponse>> crearFranquicia(
            @Valid @RequestBody CrearFranquiciaRequest request) {
        return crearFranquiciaService.ejecutar(request.nombre())
                .map(resultado -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(new FranquiciaResponse(resultado.id(), resultado.nombre())));
    }

    /**
     * Actualiza el nombre de una franquicia existente.
     */
    @PatchMapping(path = "/{franquiciaId}/nombre", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Actualizar nombre de franquicia",
            description = "Actualiza el nombre de una franquicia existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Franquicia actualizada",
                    content = @Content(schema = @Schema(implementation = FranquiciaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Franquicia no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto por nombre de franquicia duplicado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public Mono<ResponseEntity<FranquiciaResponse>> actualizarNombreFranquicia(
            @PathVariable UUID franquiciaId,
            @Valid @RequestBody ActualizarNombreFranquiciaRequest request) {
        return actualizarNombreFranquiciaService.ejecutar(franquiciaId, request.nombre())
                .map(resultado -> ResponseEntity.ok(new FranquiciaResponse(resultado.id(), resultado.nombre())));
    }

    /**
     * Lista todas las franquicias de forma independiente.
     */
    @GetMapping
    @Operation(
            summary = "Listar franquicias",
            description = "Retorna el listado completo de franquicias registradas."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Listado de franquicias",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FranquiciaResponse.class)))
    )
    public Mono<ResponseEntity<List<FranquiciaResponse>>> listarFranquicias() {
        return listarFranquiciasService.ejecutar()
                .map(this::mapearFranquicias)
                .map(ResponseEntity::ok);
    }

    /**
     * Obtiene todas las sucursales de una franquicia y, para cada una,
     * el producto con mayor stock cuando exista.
     */
    @GetMapping("/{franquiciaId}/productos/mayor-stock-por-sucursal")
    @Operation(
            summary = "Consultar mayor stock por sucursal",
            description = "Obtiene un producto por sucursal con mayor stock para la franquicia indicada."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consulta procesada correctamente",
                    content = @Content(schema = @Schema(implementation = MayorStockPorSucursalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador de franquicia invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Franquicia no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public Mono<ResponseEntity<MayorStockPorSucursalResponse>> consultarMayorStockPorSucursal(
            @PathVariable UUID franquiciaId) {
        return consultarMayorStockPorSucursalService.ejecutar(franquiciaId)
                .map(this::mapearRespuestaMayorStock)
                .map(ResponseEntity::ok);
    }

    /**
     * Obtiene un listado plano de productos ganadores por sucursal para la franquicia indicada.
     */
    @GetMapping("/{franquiciaId}/productos/mayor-stock-por-sucursal/listado")
    @Operation(
            summary = "Consultar mayor stock por sucursal (listado plano)",
            description = "Obtiene el producto con mayor stock por sucursal como listado plano, incluyendo a que sucursal pertenece cada producto."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consulta procesada correctamente",
                    content = @Content(schema = @Schema(implementation = ListadoMayorStockPorSucursalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador de franquicia invalido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Franquicia no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public Mono<ResponseEntity<ListadoMayorStockPorSucursalResponse>> consultarMayorStockPorSucursalListado(
            @PathVariable UUID franquiciaId) {
        return consultarMayorStockPorSucursalService.ejecutar(franquiciaId)
                .map(this::mapearRespuestaMayorStockListado)
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

    private ListadoMayorStockPorSucursalResponse mapearRespuestaMayorStockListado(
            ConsultaMayorStockPorSucursalResultado resultado) {
        return new ListadoMayorStockPorSucursalResponse(
                resultado.franquiciaId(),
                resultado.franquiciaNombre(),
                resultado.sucursales().stream()
                        .flatMap(sucursal -> sucursal.productos().stream()
                                .map(producto -> mapearProductoMayorStockListado(sucursal, producto)))
                        .toList()
        );
    }

    private ProductoMayorStockPorSucursalListadoResponse mapearProductoMayorStockListado(
            SucursalMayorStockPorSucursalResultado sucursalResultado,
            ProductoMayorStockPorSucursalResultado productoResultado) {
        return new ProductoMayorStockPorSucursalListadoResponse(
                sucursalResultado.sucursalId(),
                sucursalResultado.sucursalNombre(),
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
