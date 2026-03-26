package com.accenture.franquicias.producto.infrastructure.output.persistence.entity;

import com.accenture.franquicias.compartido.infrastructure.persistence.EntidadAuditada;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.entity.SucursalEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Entidad persistente para productos ofertados por una sucursal.
 *
 * <p>La unicidad por sucursal y nombre se refuerza con una restriccion
 * a nivel de tabla para evitar duplicados incluso en escenarios concurrentes.</p>
 */
@Entity
@Table(
        name = "productos",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_productos_sucursal_nombre", columnNames = {"sucursal_id", "nombre"})
        }
)
public class ProductoEntity extends EntidadAuditada {

    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private UUID id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @NotNull(message = "El producto debe pertenecer a una sucursal")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private SucursalEntity sucursal;

    public ProductoEntity() {
        // Constructor vacio requerido por JPA.
    }

    public ProductoEntity(String nombre, Integer stock, SucursalEntity sucursal) {
        this.nombre = nombre;
        this.stock = stock;
        this.sucursal = sucursal;
    }

    /**
     * Normaliza y valida campos para garantizar reglas del contrato.
     */
    @PrePersist
    @PreUpdate
    void prepararPersistencia() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }

        this.nombre = this.nombre == null ? null : this.nombre.trim();
        if (this.nombre == null || this.nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }

        if (this.stock == null || this.stock < 0) {
            throw new IllegalArgumentException("El stock debe ser mayor o igual a 0");
        }

        if (this.sucursal == null) {
            throw new IllegalArgumentException("El producto debe pertenecer a una sucursal");
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public SucursalEntity getSucursal() {
        return sucursal;
    }

    public void setSucursal(SucursalEntity sucursal) {
        this.sucursal = sucursal;
    }
}
