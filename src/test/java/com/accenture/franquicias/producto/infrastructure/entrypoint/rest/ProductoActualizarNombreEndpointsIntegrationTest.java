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
 * Pruebas de integracion para el endpoint de actualizacion de nombre de producto.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.flyway.enabled=false")
@AutoConfigureWebTestClient
class ProductoActualizarNombreEndpointsIntegrationTest {

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
    void debeActualizarNombreDeProductoYResponder200() {
        SucursalEntity sucursal = crearSucursal("Franquicia Centro", "Sucursal Norte");
        ProductoEntity producto = productoRepository.saveAndFlush(new ProductoEntity("Teclado", 12, sucursal));

        webTestClient.patch()
                .uri("/api/v1/productos/{productoId}/nombre", producto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Teclado Mecanico"
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(producto.getId().toString())
                .jsonPath("$.nombre").isEqualTo("Teclado Mecanico")
                .jsonPath("$.stock").isEqualTo(12)
                .jsonPath("$.sucursalId").isEqualTo(sucursal.getId().toString());

        assertThat(productoRepository.findById(producto.getId()))
                .isPresent()
                .get()
                .extracting(ProductoEntity::getNombre)
                .isEqualTo("Teclado Mecanico");
    }

    @Test
    void debeRetornar404CuandoProductoNoExisteAlActualizarNombre() {
        UUID productoIdInexistente = UUID.randomUUID();

        webTestClient.patch()
                .uri("/api/v1/productos/{productoId}/nombre", productoIdInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Monitor"
                        }
                        """)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found")
                .jsonPath("$.message").isEqualTo("Producto no encontrado")
                .jsonPath("$.path").isEqualTo("/api/v1/productos/" + productoIdInexistente + "/nombre");
    }

    @Test
    void debeRetornar409CuandoNombreActualizadoSeDuplicaEnLaMismaSucursal() {
        SucursalEntity sucursal = crearSucursal("Franquicia Centro", "Sucursal Norte");
        productoRepository.saveAndFlush(new ProductoEntity("Monitor", 20, sucursal));
        ProductoEntity producto = productoRepository.saveAndFlush(new ProductoEntity("Teclado", 10, sucursal));

        webTestClient.patch()
                .uri("/api/v1/productos/{productoId}/nombre", producto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Monitor"
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Conflict")
                .jsonPath("$.message").isEqualTo("Ya existe un producto con ese nombre en la sucursal")
                .jsonPath("$.path").isEqualTo("/api/v1/productos/" + producto.getId() + "/nombre");
    }

    @Test
    void debeRetornar400CuandoNombreActualizadoEsInvalido() {
        SucursalEntity sucursal = crearSucursal("Franquicia Centro", "Sucursal Norte");
        ProductoEntity producto = productoRepository.saveAndFlush(new ProductoEntity("Teclado", 10, sucursal));

        webTestClient.patch()
                .uri("/api/v1/productos/{productoId}/nombre", producto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "   "
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.message").isEqualTo("El nombre es obligatorio")
                .jsonPath("$.path").isEqualTo("/api/v1/productos/" + producto.getId() + "/nombre");
    }

    private SucursalEntity crearSucursal(String nombreFranquicia, String nombreSucursal) {
        FranquiciaEntity franquicia = franquiciaRepository.saveAndFlush(new FranquiciaEntity(nombreFranquicia));
        return sucursalRepository.saveAndFlush(new SucursalEntity(nombreSucursal, franquicia));
    }
}
