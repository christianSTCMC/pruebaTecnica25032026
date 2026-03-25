package com.accenture.franquicias.producto.infrastructure.entrypoint.rest;

import com.accenture.franquicias.franquicia.infrastructure.output.persistence.entity.FranquiciaEntity;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.repository.FranquiciaRepositoryJpa;
import com.accenture.franquicias.producto.infrastructure.output.persistence.entity.ProductoEntity;
import com.accenture.franquicias.producto.infrastructure.output.persistence.repository.ProductoRepositoryJpa;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.entity.SucursalEntity;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.repository.SucursalRepositoryJpa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de endpoints de Fase 3 para alta, eliminacion y actualizacion de stock.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.flyway.enabled=false")
@AutoConfigureWebTestClient
class ProductoEndpointsIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private FranquiciaRepositoryJpa franquiciaRepository;

    @Autowired
    private SucursalRepositoryJpa sucursalRepository;

    @Autowired
    private ProductoRepositoryJpa productoRepository;

    @BeforeEach
    void limpiarDatos() {
        productoRepository.deleteAll();
        sucursalRepository.deleteAll();
        franquiciaRepository.deleteAll();
    }

    @Test
    void debeCrearProductoYResponder201() {
        SucursalEntity sucursal = crearSucursal("Franquicia Centro", "Sucursal Norte");

        webTestClient.post()
                .uri("/api/v1/sucursales/{sucursalId}/productos", sucursal.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Laptop Gamer",
                          "stock": 15
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nombre").isEqualTo("Laptop Gamer")
                .jsonPath("$.stock").isEqualTo(15)
                .jsonPath("$.sucursalId").isEqualTo(sucursal.getId().toString());
    }

    @Test
    void debeRetornar404CuandoSucursalNoExisteAlCrearProducto() {
        UUID sucursalIdInexistente = UUID.randomUUID();

        webTestClient.post()
                .uri("/api/v1/sucursales/{sucursalId}/productos", sucursalIdInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Monitor",
                          "stock": 20
                        }
                        """)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found")
                .jsonPath("$.message").isEqualTo("Sucursal no encontrada")
                .jsonPath("$.path").isEqualTo("/api/v1/sucursales/" + sucursalIdInexistente + "/productos");
    }

    @Test
    void debeRetornar409CuandoNombreDeProductoSeRepiteEnLaSucursal() {
        SucursalEntity sucursal = crearSucursal("Franquicia Centro", "Sucursal Norte");
        productoRepository.saveAndFlush(new ProductoEntity("Laptop Gamer", 10, sucursal));

        webTestClient.post()
                .uri("/api/v1/sucursales/{sucursalId}/productos", sucursal.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Laptop Gamer",
                          "stock": 30
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Conflict")
                .jsonPath("$.message").isEqualTo("Ya existe un producto con ese nombre en la sucursal")
                .jsonPath("$.path").isEqualTo("/api/v1/sucursales/" + sucursal.getId() + "/productos");
    }

    @Test
    void debeRetornar400CuandoStockDeCreacionEsNegativo() {
        SucursalEntity sucursal = crearSucursal("Franquicia Centro", "Sucursal Norte");

        webTestClient.post()
                .uri("/api/v1/sucursales/{sucursalId}/productos", sucursal.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Laptop Gamer",
                          "stock": -1
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.message").isEqualTo("El stock debe ser mayor o igual a 0")
                .jsonPath("$.path").isEqualTo("/api/v1/sucursales/" + sucursal.getId() + "/productos");
    }

    @Test
    void debeEliminarProductoCuandoPerteneceALaSucursal() {
        SucursalEntity sucursal = crearSucursal("Franquicia Centro", "Sucursal Norte");
        ProductoEntity producto = productoRepository.saveAndFlush(new ProductoEntity("Teclado", 5, sucursal));

        webTestClient.delete()
                .uri("/api/v1/sucursales/{sucursalId}/productos/{productoId}", sucursal.getId(), producto.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        assertThat(productoRepository.findById(producto.getId())).isEmpty();
    }

    @Test
    void debeRetornar409CuandoProductoNoPerteneceALaSucursalIndicada() {
        SucursalEntity sucursalNorte = crearSucursal("Franquicia Centro", "Sucursal Norte");
        SucursalEntity sucursalSur = crearSucursal("Franquicia Centro", "Sucursal Sur");
        ProductoEntity producto = productoRepository.saveAndFlush(new ProductoEntity("Mouse", 8, sucursalSur));

        webTestClient.delete()
                .uri("/api/v1/sucursales/{sucursalId}/productos/{productoId}", sucursalNorte.getId(), producto.getId())
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Conflict")
                .jsonPath("$.message").isEqualTo("El producto no pertenece a la sucursal indicada")
                .jsonPath("$.path").isEqualTo("/api/v1/sucursales/" + sucursalNorte.getId()
                        + "/productos/" + producto.getId());
    }

    @Test
    void debeActualizarStockDeProductoYResponder200() {
        SucursalEntity sucursal = crearSucursal("Franquicia Centro", "Sucursal Norte");
        ProductoEntity producto = productoRepository.saveAndFlush(new ProductoEntity("Mouse", 12, sucursal));

        webTestClient.patch()
                .uri("/api/v1/productos/{productoId}/stock", producto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "stock": 30
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(producto.getId().toString())
                .jsonPath("$.nombre").isEqualTo("Mouse")
                .jsonPath("$.stock").isEqualTo(30)
                .jsonPath("$.sucursalId").isEqualTo(sucursal.getId().toString());

        assertThat(productoRepository.findById(producto.getId()))
                .isPresent()
                .get()
                .extracting(ProductoEntity::getStock)
                .isEqualTo(30);
    }

    @Test
    void debeRetornar404CuandoProductoNoExisteAlActualizarStock() {
        UUID productoIdInexistente = UUID.randomUUID();

        webTestClient.patch()
                .uri("/api/v1/productos/{productoId}/stock", productoIdInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "stock": 7
                        }
                        """)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found")
                .jsonPath("$.message").isEqualTo("Producto no encontrado")
                .jsonPath("$.path").isEqualTo("/api/v1/productos/" + productoIdInexistente + "/stock");
    }

    @Test
    void debeRetornar400CuandoStockActualizadoEsNegativo() {
        SucursalEntity sucursal = crearSucursal("Franquicia Centro", "Sucursal Norte");
        ProductoEntity producto = productoRepository.saveAndFlush(new ProductoEntity("Mouse", 12, sucursal));

        webTestClient.patch()
                .uri("/api/v1/productos/{productoId}/stock", producto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "stock": -5
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.message").isEqualTo("El stock debe ser mayor o igual a 0")
                .jsonPath("$.path").isEqualTo("/api/v1/productos/" + producto.getId() + "/stock");
    }

    @Test
    void debeRetornar400CuandoProductoIdNoEsUuidValidoAlActualizarStock() {
        webTestClient.patch()
                .uri("/api/v1/productos/id-invalido/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "stock": 10
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.message").isEqualTo("Solicitud invalida")
                .jsonPath("$.path").isEqualTo("/api/v1/productos/id-invalido/stock");
    }

    @Test
    void debeRetornar400CuandoStockEsNuloAlActualizarStock() {
        SucursalEntity sucursal = crearSucursal("Franquicia Centro", "Sucursal Norte");
        ProductoEntity producto = productoRepository.saveAndFlush(new ProductoEntity("Mouse", 12, sucursal));

        webTestClient.patch()
                .uri("/api/v1/productos/{productoId}/stock", producto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.message").isEqualTo("El stock es obligatorio")
                .jsonPath("$.path").isEqualTo("/api/v1/productos/" + producto.getId() + "/stock");
    }

    private SucursalEntity crearSucursal(String nombreFranquicia, String nombreSucursal) {
        // Se hace unico el nombre de franquicia para evitar colisiones al crear varias sucursales en pruebas.
        FranquiciaEntity franquicia = franquiciaRepository.saveAndFlush(
                new FranquiciaEntity(nombreFranquicia + " " + UUID.randomUUID())
        );
        return sucursalRepository.saveAndFlush(new SucursalEntity(nombreSucursal, franquicia));
    }
}
