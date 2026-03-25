package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest;

import com.accenture.franquicias.franquicia.infrastructure.output.persistence.entity.FranquiciaEntity;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.repository.FranquiciaRepositoryJpa;
import com.accenture.franquicias.producto.infrastructure.output.persistence.repository.ProductoRepositoryJpa;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.repository.SucursalRepositoryJpa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

/**
 * Pruebas de endpoints de Fase 2 para franquicias y sucursales.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.flyway.enabled=false")
@AutoConfigureWebTestClient
class FranquiciaSucursalEndpointsIntegrationTest {

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
    void debeCrearFranquiciaYResponder201() {
        webTestClient.post()
                .uri("/api/v1/franquicias")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Franquicia Centro"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nombre").isEqualTo("Franquicia Centro");
    }

    @Test
    void debeRetornar400CuandoNombreDeFranquiciaEsInvalido() {
        webTestClient.post()
                .uri("/api/v1/franquicias")
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
                .jsonPath("$.path").isEqualTo("/api/v1/franquicias");
    }

    @Test
    void debeCrearSucursalCuandoLaFranquiciaExiste() {
        FranquiciaEntity franquicia = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Norte"));

        webTestClient.post()
                .uri("/api/v1/franquicias/{franquiciaId}/sucursales", franquicia.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Sucursal Centro"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nombre").isEqualTo("Sucursal Centro")
                .jsonPath("$.franquiciaId").isEqualTo(franquicia.getId().toString());
    }

    @Test
    void debeRetornar404CuandoLaFranquiciaNoExiste() {
        UUID franquiciaIdInexistente = UUID.randomUUID();

        webTestClient.post()
                .uri("/api/v1/franquicias/{franquiciaId}/sucursales", franquiciaIdInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Sucursal Fantasma"
                        }
                        """)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found")
                .jsonPath("$.message").isEqualTo("Franquicia no encontrada")
                .jsonPath("$.path").isEqualTo("/api/v1/franquicias/" + franquiciaIdInexistente + "/sucursales");
    }

    @Test
    void debeRetornar400CuandoFranquiciaIdNoEsUuidValido() {
        webTestClient.post()
                .uri("/api/v1/franquicias/id-invalido/sucursales")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Sucursal Centro"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.message").isEqualTo("Solicitud invalida")
                .jsonPath("$.path").isEqualTo("/api/v1/franquicias/id-invalido/sucursales");
    }
}
