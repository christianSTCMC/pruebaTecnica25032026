package com.accenture.franquicias.producto.infrastructure.entrypoint.rest;

import com.accenture.franquicias.compartido.infrastructure.entrypoint.rest.error.ApiErrorResponse;
import com.accenture.franquicias.producto.application.dto.ProductoListado;
import com.accenture.franquicias.producto.application.dto.ProductoResultado;
import com.accenture.franquicias.producto.application.service.ActualizarNombreProductoService;
import com.accenture.franquicias.producto.application.service.ActualizarStockProductoService;
import com.accenture.franquicias.producto.application.service.CrearProductoService;
import com.accenture.franquicias.producto.application.service.EliminarProductoService;
import com.accenture.franquicias.producto.application.service.ListarProductosService;
import com.accenture.franquicias.producto.infrastructure.entrypoint.rest.dto.ActualizarNombreProductoRequest;
import com.accenture.franquicias.producto.infrastructure.entrypoint.rest.dto.ActualizarStockProductoRequest;
import com.accenture.franquicias.producto.infrastructure.entrypoint.rest.dto.CrearProductoRequest;
import com.accenture.franquicias.producto.infrastructure.entrypoint.rest.dto.ProductoResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Controlador HTTP para operaciones de productos expuestas en la Fase 3.
 */
@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Productos", description = "Operaciones para gestionar productos por sucursal")
public class ProductoController {

    private final CrearProductoService crearProductoService;
    private final EliminarProductoService eliminarProductoService;
    private final ActualizarNombreProductoService actualizarNombreProductoService;
    private final ActualizarStockProductoService actualizarStockProductoService;
    private final ListarProductosService listarProductosService;

    public ProductoController(
            CrearProductoService crearProductoService,
            EliminarProductoService eliminarProductoService,
            ActualizarNombreProductoService actualizarNombreProductoService,
            ActualizarStockProductoService actualizarStockProductoService,
            ListarProductosService listarProductosService) {
        this.crearProductoService = crearProductoService;
        this.eliminarProductoService = eliminarProductoService;
        this.actualizarNombreProductoService = actualizarNombreProductoService;
        this.actualizarStockProductoService = actualizarStockProductoService;
        this.listarProductosService = listarProductosService;
    }

    /**
     * Crea un producto en la sucursal indicada.
     */
    @PostMapping(path = "/sucursales/{sucursalId}/productos", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Crear producto",
            description = "Crea un producto en una sucursal existente validando nombre unico por sucursal."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Producto creado",
                    content = @Content(schema = @Schema(implementation = ProductoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sucursal no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto de negocio por duplicidad",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public Mono<ResponseEntity<ProductoResponse>> crearProducto(
            @PathVariable UUID sucursalId,
            @Valid @RequestBody CrearProductoRequest request) {
        return crearProductoService.ejecutar(sucursalId, request.nombre(), request.stock())
                .map(resultado -> ResponseEntity.status(HttpStatus.CREATED).body(mapearRespuesta(resultado)));
    }

    /**
     * Elimina un producto validando que pertenezca a la sucursal del path.
     */
    @DeleteMapping(path = "/sucursales/{sucursalId}/productos/{productoId}")
    @Operation(
            summary = "Eliminar producto",
            description = "Elimina un producto siempre que pertenezca a la sucursal indicada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto o sucursal no encontrados",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El producto no pertenece a la sucursal indicada",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public Mono<ResponseEntity<Void>> eliminarProducto(
            @PathVariable UUID sucursalId,
            @PathVariable UUID productoId) {
        return eliminarProductoService.ejecutar(sucursalId, productoId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    /**
     * Modifica el stock del producto indicado.
     */
    @PatchMapping(path = "/productos/{productoId}/stock", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Actualizar stock de producto",
            description = "Actualiza el stock de un producto existente validando rango de 0 a 2147483647."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Stock actualizado",
                    content = @Content(schema = @Schema(implementation = ProductoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public Mono<ResponseEntity<ProductoResponse>> actualizarStock(
            @PathVariable UUID productoId,
            @Valid @RequestBody ActualizarStockProductoRequest request) {
        // La conversion es segura porque el request ya valida el maximo permitido por INT en DB.
        return actualizarStockProductoService.ejecutar(productoId, Math.toIntExact(request.stock()))
                .map(resultado -> ResponseEntity.ok(mapearRespuesta(resultado)));
    }

    /**
     * Modifica el nombre del producto indicado.
     */
    @PatchMapping(path = "/productos/{productoId}/nombre", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Actualizar nombre de producto",
            description = "Actualiza el nombre de un producto validando que no se repita en su sucursal."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Nombre actualizado",
                    content = @Content(schema = @Schema(implementation = ProductoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto de negocio por duplicidad",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    public Mono<ResponseEntity<ProductoResponse>> actualizarNombre(
            @PathVariable UUID productoId,
            @Valid @RequestBody ActualizarNombreProductoRequest request) {
        return actualizarNombreProductoService.ejecutar(productoId, request.nombre())
                .map(resultado -> ResponseEntity.ok(mapearRespuesta(resultado)));
    }

    /**
     * Lista todos los productos de forma independiente.
     */
    @GetMapping(path = "/productos")
    @Operation(
            summary = "Listar productos",
            description = "Retorna el listado completo de productos registrados."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Listado de productos",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductoResponse.class)))
    )
    public Mono<ResponseEntity<List<ProductoResponse>>> listarProductos() {
        return listarProductosService.ejecutar()
                .map(this::mapearListadoProductos)
                .map(ResponseEntity::ok);
    }

    private ProductoResponse mapearRespuesta(ProductoResultado resultado) {
        return new ProductoResponse(
                resultado.id(),
                resultado.nombre(),
                resultado.stock(),
                resultado.sucursalId()
        );
    }

    private List<ProductoResponse> mapearListadoProductos(List<ProductoListado> productos) {
        return productos.stream()
                .map(producto -> new ProductoResponse(
                        producto.id(),
                        producto.nombre(),
                        producto.stock(),
                        producto.sucursalId()))
                .toList();
    }
}
