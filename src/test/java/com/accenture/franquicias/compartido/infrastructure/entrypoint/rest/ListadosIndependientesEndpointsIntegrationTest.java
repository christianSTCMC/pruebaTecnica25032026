package com.accenture.franquicias.compartido.infrastructure.entrypoint.rest;

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
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Pruebas de integracion para listados GET independientes de franquicias, sucursales y productos.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.flyway.enabled=false")
@AutoConfigureWebTestClient
class ListadosIndependientesEndpointsIntegrationTest {

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
    void debeListarFranquiciasDeFormaIndependiente() {
        FranquiciaEntity franquiciaSur = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Sur"));
        FranquiciaEntity franquiciaCentro = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Centro"));
        FranquiciaEntity franquiciaNorte = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Norte"));

        webTestClient.get()
                .uri("/api/v1/franquicias")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[0].id").isEqualTo(franquiciaCentro.getId().toString())
                .jsonPath("$[0].nombre").isEqualTo("Franquicia Centro")
                .jsonPath("$[1].id").isEqualTo(franquiciaNorte.getId().toString())
                .jsonPath("$[1].nombre").isEqualTo("Franquicia Norte")
                .jsonPath("$[2].id").isEqualTo(franquiciaSur.getId().toString())
                .jsonPath("$[2].nombre").isEqualTo("Franquicia Sur");
    }

    @Test
    void debeListarSucursalesDeFormaIndependiente() {
        FranquiciaEntity franquiciaCentro = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Centro"));
        FranquiciaEntity franquiciaNorte = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Norte"));

        SucursalEntity sucursalB = sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal B", franquiciaCentro));
        SucursalEntity sucursalA = sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal A", franquiciaNorte));
        SucursalEntity sucursalC = sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal C", franquiciaCentro));

        webTestClient.get()
                .uri("/api/v1/sucursales")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[0].id").isEqualTo(sucursalA.getId().toString())
                .jsonPath("$[0].nombre").isEqualTo("Sucursal A")
                .jsonPath("$[0].franquiciaId").isEqualTo(franquiciaNorte.getId().toString())
                .jsonPath("$[1].id").isEqualTo(sucursalB.getId().toString())
                .jsonPath("$[1].nombre").isEqualTo("Sucursal B")
                .jsonPath("$[1].franquiciaId").isEqualTo(franquiciaCentro.getId().toString())
                .jsonPath("$[2].id").isEqualTo(sucursalC.getId().toString())
                .jsonPath("$[2].nombre").isEqualTo("Sucursal C")
                .jsonPath("$[2].franquiciaId").isEqualTo(franquiciaCentro.getId().toString());
    }

    @Test
    void debeListarProductosDeFormaIndependiente() {
        FranquiciaEntity franquicia = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Centro"));
        SucursalEntity sucursalNorte = sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal Norte", franquicia));
        SucursalEntity sucursalSur = sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal Sur", franquicia));

        ProductoEntity teclado = productoRepository.saveAndFlush(new ProductoEntity("Teclado", 15, sucursalNorte));
        ProductoEntity audifonos = productoRepository.saveAndFlush(new ProductoEntity("Audifonos", 44, sucursalSur));
        ProductoEntity monitor = productoRepository.saveAndFlush(new ProductoEntity("Monitor", 22, sucursalNorte));

        webTestClient.get()
                .uri("/api/v1/productos")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[0].id").isEqualTo(audifonos.getId().toString())
                .jsonPath("$[0].nombre").isEqualTo("Audifonos")
                .jsonPath("$[0].stock").isEqualTo(44)
                .jsonPath("$[0].sucursalId").isEqualTo(sucursalSur.getId().toString())
                .jsonPath("$[1].id").isEqualTo(monitor.getId().toString())
                .jsonPath("$[1].nombre").isEqualTo("Monitor")
                .jsonPath("$[1].stock").isEqualTo(22)
                .jsonPath("$[1].sucursalId").isEqualTo(sucursalNorte.getId().toString())
                .jsonPath("$[2].id").isEqualTo(teclado.getId().toString())
                .jsonPath("$[2].nombre").isEqualTo("Teclado")
                .jsonPath("$[2].stock").isEqualTo(15)
                .jsonPath("$[2].sucursalId").isEqualTo(sucursalNorte.getId().toString());
    }
}
