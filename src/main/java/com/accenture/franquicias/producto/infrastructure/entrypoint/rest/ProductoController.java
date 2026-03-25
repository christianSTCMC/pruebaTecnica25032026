package com.accenture.franquicias.producto.infrastructure.entrypoint.rest;

import com.accenture.franquicias.producto.application.dto.ProductoListado;
import com.accenture.franquicias.producto.application.dto.ProductoResultado;
import com.accenture.franquicias.producto.application.service.ActualizarStockProductoService;
import com.accenture.franquicias.producto.application.service.CrearProductoService;
import com.accenture.franquicias.producto.application.service.EliminarProductoService;
import com.accenture.franquicias.producto.application.service.ListarProductosService;
import com.accenture.franquicias.producto.infrastructure.entrypoint.rest.dto.ActualizarStockProductoRequest;
import com.accenture.franquicias.producto.infrastructure.entrypoint.rest.dto.CrearProductoRequest;
import com.accenture.franquicias.producto.infrastructure.entrypoint.rest.dto.ProductoResponse;
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
public class ProductoController {

    private final CrearProductoService crearProductoService;
    private final EliminarProductoService eliminarProductoService;
    private final ActualizarStockProductoService actualizarStockProductoService;
    private final ListarProductosService listarProductosService;

    public ProductoController(
            CrearProductoService crearProductoService,
            EliminarProductoService eliminarProductoService,
            ActualizarStockProductoService actualizarStockProductoService,
            ListarProductosService listarProductosService) {
        this.crearProductoService = crearProductoService;
        this.eliminarProductoService = eliminarProductoService;
        this.actualizarStockProductoService = actualizarStockProductoService;
        this.listarProductosService = listarProductosService;
    }

    /**
     * Crea un producto en la sucursal indicada.
     */
    @PostMapping(path = "/sucursales/{sucursalId}/productos", consumes = MediaType.APPLICATION_JSON_VALUE)
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
    public Mono<ResponseEntity<ProductoResponse>> actualizarStock(
            @PathVariable UUID productoId,
            @Valid @RequestBody ActualizarStockProductoRequest request) {
        return actualizarStockProductoService.ejecutar(productoId, request.stock())
                .map(resultado -> ResponseEntity.ok(mapearRespuesta(resultado)));
    }

    /**
     * Lista todos los productos de forma independiente.
     */
    @GetMapping(path = "/productos")
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
