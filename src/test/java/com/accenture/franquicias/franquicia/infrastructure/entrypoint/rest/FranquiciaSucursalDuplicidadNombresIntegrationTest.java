package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest;

import com.accenture.franquicias.franquicia.infrastructure.output.persistence.entity.FranquiciaEntity;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.repository.FranquiciaRepositoryJpa;
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

/**
 * Pruebas de integracion para conflictos 409 por duplicidad de nombres.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.flyway.enabled=false")
@AutoConfigureWebTestClient
class FranquiciaSucursalDuplicidadNombresIntegrationTest {

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
    void debeRetornar409CuandoNombreDeFranquiciaYaExisteAlCrear() {
        franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Centro"));

        webTestClient.post()
                .uri("/api/v1/franquicias")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Franquicia Centro"
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Conflict")
                .jsonPath("$.message").isEqualTo("Ya existe una franquicia con ese nombre")
                .jsonPath("$.path").isEqualTo("/api/v1/franquicias");
    }

    @Test
    void debeRetornar409CuandoNombreDeFranquiciaYaExisteAlActualizar() {
        FranquiciaEntity franquiciaA = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia A"));
        franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia B"));

        webTestClient.patch()
                .uri("/api/v1/franquicias/{franquiciaId}/nombre", franquiciaA.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Franquicia B"
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Conflict")
                .jsonPath("$.message").isEqualTo("Ya existe una franquicia con ese nombre")
                .jsonPath("$.path").isEqualTo("/api/v1/franquicias/" + franquiciaA.getId() + "/nombre");
    }

    @Test
    void debeRetornar409CuandoNombreDeSucursalYaExisteAlCrear() {
        FranquiciaEntity franquiciaA = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia A"));
        FranquiciaEntity franquiciaB = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia B"));
        sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal Centro", franquiciaA));

        webTestClient.post()
                .uri("/api/v1/franquicias/{franquiciaId}/sucursales", franquiciaB.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Sucursal Centro"
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Conflict")
                .jsonPath("$.message").isEqualTo("Ya existe una sucursal con ese nombre")
                .jsonPath("$.path").isEqualTo("/api/v1/franquicias/" + franquiciaB.getId() + "/sucursales");
    }

    @Test
    void debeRetornar409CuandoNombreDeSucursalYaExisteAlActualizar() {
        FranquiciaEntity franquicia = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia A"));
        SucursalEntity sucursalA = sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal A", franquicia));
        sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal B", franquicia));

        webTestClient.patch()
                .uri("/api/v1/sucursales/{sucursalId}/nombre", sucursalA.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Sucursal B"
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Conflict")
                .jsonPath("$.message").isEqualTo("Ya existe una sucursal con ese nombre")
                .jsonPath("$.path").isEqualTo("/api/v1/sucursales/" + sucursalA.getId() + "/nombre");
    }
}
