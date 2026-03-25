package com.accenture.franquicias.franquicia.infrastructure.entrypoint.rest;

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
 * Pruebas de integracion para la consulta de mayor stock por sucursal (Fase 4).
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.flyway.enabled=false")
@AutoConfigureWebTestClient
class FranquiciaMayorStockPorSucursalIntegrationTest {

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
    void debeRetornarUnProductoPorCadaSucursalConProductos() {
        FranquiciaEntity franquicia = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Centro"));

        SucursalEntity sucursalNorte = sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal Norte", franquicia));
        SucursalEntity sucursalSur = sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal Sur", franquicia));
        sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal Vacia", franquicia));

        ProductoEntity audifonos = productoRepository.saveAndFlush(new ProductoEntity("Audifonos", 30, sucursalNorte));
        productoRepository.saveAndFlush(new ProductoEntity("Teclado", 20, sucursalNorte));
        ProductoEntity mouse = productoRepository.saveAndFlush(new ProductoEntity("Mouse Inalambrico", 44, sucursalSur));
        productoRepository.saveAndFlush(new ProductoEntity("Monitor", 8, sucursalSur));

        webTestClient.get()
                .uri("/api/v1/franquicias/{franquiciaId}/productos/mayor-stock-por-sucursal", franquicia.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.franquiciaId").isEqualTo(franquicia.getId().toString())
                .jsonPath("$.franquiciaNombre").isEqualTo("Franquicia Centro")
                .jsonPath("$.sucursales.length()").isEqualTo(2)
                .jsonPath("$.sucursales[0].sucursalId").isEqualTo(sucursalNorte.getId().toString())
                .jsonPath("$.sucursales[0].sucursalNombre").isEqualTo("Sucursal Norte")
                .jsonPath("$.sucursales[0].productos.length()").isEqualTo(1)
                .jsonPath("$.sucursales[0].productos[0].productoId").isEqualTo(audifonos.getId().toString())
                .jsonPath("$.sucursales[0].productos[0].productoNombre").isEqualTo("Audifonos")
                .jsonPath("$.sucursales[0].productos[0].stock").isEqualTo(30)
                .jsonPath("$.sucursales[1].sucursalId").isEqualTo(sucursalSur.getId().toString())
                .jsonPath("$.sucursales[1].sucursalNombre").isEqualTo("Sucursal Sur")
                .jsonPath("$.sucursales[1].productos.length()").isEqualTo(1)
                .jsonPath("$.sucursales[1].productos[0].productoId").isEqualTo(mouse.getId().toString())
                .jsonPath("$.sucursales[1].productos[0].productoNombre").isEqualTo("Mouse Inalambrico")
                .jsonPath("$.sucursales[1].productos[0].stock").isEqualTo(44);
    }

    @Test
    void debeResolverEmpatePorStockConNombreAscendente() {
        FranquiciaEntity franquicia = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Centro"));
        SucursalEntity sucursalNorte = sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal Norte", franquicia));

        ProductoEntity audifonos = productoRepository.saveAndFlush(new ProductoEntity("Audifonos", 30, sucursalNorte));
        productoRepository.saveAndFlush(new ProductoEntity("Laptop Gamer", 30, sucursalNorte));
        productoRepository.saveAndFlush(new ProductoEntity("Teclado", 10, sucursalNorte));

        webTestClient.get()
                .uri("/api/v1/franquicias/{franquiciaId}/productos/mayor-stock-por-sucursal", franquicia.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.franquiciaId").isEqualTo(franquicia.getId().toString())
                .jsonPath("$.franquiciaNombre").isEqualTo("Franquicia Centro")
                .jsonPath("$.sucursales.length()").isEqualTo(1)
                .jsonPath("$.sucursales[0].sucursalId").isEqualTo(sucursalNorte.getId().toString())
                .jsonPath("$.sucursales[0].sucursalNombre").isEqualTo("Sucursal Norte")
                .jsonPath("$.sucursales[0].productos.length()").isEqualTo(1)
                .jsonPath("$.sucursales[0].productos[0].productoId").isEqualTo(audifonos.getId().toString())
                .jsonPath("$.sucursales[0].productos[0].productoNombre").isEqualTo("Audifonos")
                .jsonPath("$.sucursales[0].productos[0].stock").isEqualTo(30);
    }
}
