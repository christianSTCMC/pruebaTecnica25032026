package com.accenture.franquicias.producto.infrastructure.output.persistence.repository;

import com.accenture.franquicias.franquicia.infrastructure.output.persistence.entity.FranquiciaEntity;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.repository.FranquiciaRepositoryJpa;
import com.accenture.franquicias.producto.infrastructure.output.persistence.entity.ProductoEntity;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.entity.SucursalEntity;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.repository.SucursalRepositoryJpa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Valida reglas de persistencia de Fase 1 sin adelantar logica HTTP ni casos de uso.
 */
@DataJpaTest(properties = "spring.flyway.enabled=false")
class ProductoRepositoryJpaIntegrationTest {

    @Autowired
    private FranquiciaRepositoryJpa franquiciaRepository;

    @Autowired
    private SucursalRepositoryJpa sucursalRepository;

    @Autowired
    private ProductoRepositoryJpa productoRepository;

    @Test
    void debeRechazarStockNegativoDesdeLaEntidad() {
        SucursalEntity sucursal = crearSucursal("Franquicia Centro", "Sucursal Norte");

        assertThatThrownBy(() -> productoRepository.saveAndFlush(new ProductoEntity("Producto Invalido", -1, sucursal)))
                .hasRootCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stock");
    }

    @Test
    void debeRechazarNombreDuplicadoDeProductoEnLaMismaSucursal() {
        SucursalEntity sucursal = crearSucursal("Franquicia Centro", "Sucursal Norte");

        productoRepository.saveAndFlush(new ProductoEntity("Laptop Gamer", 10, sucursal));

        assertThatThrownBy(() -> productoRepository.saveAndFlush(new ProductoEntity("Laptop Gamer", 25, sucursal)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void debeObtenerProductoMayorStockPorSucursalConDesempateAlfabetico() {
        FranquiciaEntity franquicia = franquiciaRepository.saveAndFlush(new FranquiciaEntity("Franquicia Centro"));

        SucursalEntity sucursalNorte = sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal Norte", franquicia));
        SucursalEntity sucursalSur = sucursalRepository.saveAndFlush(new SucursalEntity("Sucursal Sur", franquicia));

        productoRepository.saveAndFlush(new ProductoEntity("Teclado", 25, sucursalNorte));
        productoRepository.saveAndFlush(new ProductoEntity("Audifonos", 30, sucursalNorte));
        productoRepository.saveAndFlush(new ProductoEntity("Laptop Gamer", 30, sucursalNorte));
        productoRepository.saveAndFlush(new ProductoEntity("Mouse Inalambrico", 44, sucursalSur));
        productoRepository.saveAndFlush(new ProductoEntity("Monitor", 20, sucursalSur));

        List<ProductoEntity> resultado = productoRepository.findProductoMayorStockPorSucursal(franquicia.getId());

        assertThat(resultado)
                .extracting(producto -> producto.getSucursal().getNombre(), ProductoEntity::getNombre, ProductoEntity::getStock)
                .containsExactly(
                        tuple("Sucursal Norte", "Audifonos", 30),
                        tuple("Sucursal Sur", "Mouse Inalambrico", 44)
                );
    }

    private SucursalEntity crearSucursal(String nombreFranquicia, String nombreSucursal) {
        FranquiciaEntity franquicia = franquiciaRepository.saveAndFlush(new FranquiciaEntity(nombreFranquicia));
        return sucursalRepository.saveAndFlush(new SucursalEntity(nombreSucursal, franquicia));
    }
}
