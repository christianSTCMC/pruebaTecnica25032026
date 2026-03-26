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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
    void debeActualizarNombreDeFranquiciaYResponder200() {
        FranquiciaEntity franquicia = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Centro"));

        webTestClient.patch()
                .uri("/api/v1/franquicias/{franquiciaId}/nombre", franquicia.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Franquicia Centro Renombrada"
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(franquicia.getId().toString())
                .jsonPath("$.nombre").isEqualTo("Franquicia Centro Renombrada");

        assertThat(franquiciaRepository.findById(franquicia.getId()))
                .isPresent()
                .get()
                .extracting(FranquiciaEntity::getNombre)
                .isEqualTo("Franquicia Centro Renombrada");
    }

    @Test
    void debeRetornar404CuandoFranquiciaNoExisteAlActualizarNombre() {
        UUID franquiciaIdInexistente = UUID.randomUUID();

        webTestClient.patch()
                .uri("/api/v1/franquicias/{franquiciaId}/nombre", franquiciaIdInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Franquicia Fantasma"
                        }
                        """)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found")
                .jsonPath("$.message").isEqualTo("Franquicia no encontrada")
                .jsonPath("$.path").isEqualTo("/api/v1/franquicias/" + franquiciaIdInexistente + "/nombre");
    }

    @Test
    void debeRetornar400CuandoNombreDeFranquiciaEsInvalidoAlActualizar() {
        FranquiciaEntity franquicia = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Centro"));

        webTestClient.patch()
                .uri("/api/v1/franquicias/{franquiciaId}/nombre", franquicia.getId())
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
                .jsonPath("$.path").isEqualTo("/api/v1/franquicias/" + franquicia.getId() + "/nombre");
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

    @Test
    void debeActualizarNombreDeSucursalYResponder200() {
        FranquiciaEntity franquicia = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Norte"));
        SucursalEntity sucursal = sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal Centro", franquicia));

        webTestClient.patch()
                .uri("/api/v1/sucursales/{sucursalId}/nombre", sucursal.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "nombre": "Sucursal Centro Renombrada"
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(sucursal.getId().toString())
                .jsonPath("$.nombre").isEqualTo("Sucursal Centro Renombrada")
                .jsonPath("$.franquiciaId").isEqualTo(franquicia.getId().toString());

        assertThat(sucursalRepository.findById(sucursal.getId()))
                .isPresent()
                .get()
                .extracting(SucursalEntity::getNombre)
                .isEqualTo("Sucursal Centro Renombrada");
    }

    @Test
    void debeRetornar404CuandoSucursalNoExisteAlActualizarNombre() {
        UUID sucursalIdInexistente = UUID.randomUUID();

        webTestClient.patch()
                .uri("/api/v1/sucursales/{sucursalId}/nombre", sucursalIdInexistente)
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
                .jsonPath("$.message").isEqualTo("Sucursal no encontrada")
                .jsonPath("$.path").isEqualTo("/api/v1/sucursales/" + sucursalIdInexistente + "/nombre");
    }

    @Test
    void debeRetornar400CuandoNombreDeSucursalEsInvalidoAlActualizar() {
        FranquiciaEntity franquicia = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Norte"));
        SucursalEntity sucursal = sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal Centro", franquicia));

        webTestClient.patch()
                .uri("/api/v1/sucursales/{sucursalId}/nombre", sucursal.getId())
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
                .jsonPath("$.path").isEqualTo("/api/v1/sucursales/" + sucursal.getId() + "/nombre");
    }
}
