package com.accenture.franquicias.sucursal.infrastructure.output.persistence.entity;

import com.accenture.franquicias.compartido.infrastructure.persistence.EntidadAuditada;
import com.accenture.franquicias.franquicia.infrastructure.output.persistence.entity.FranquiciaEntity;
import com.accenture.franquicias.producto.infrastructure.output.persistence.entity.ProductoEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad persistente para sucursales asociadas a una franquicia.
 *
 * <p>La relacion con franquicia se marca obligatoria para reforzar que
 * una sucursal siempre pertenece a una sola franquicia.</p>
 */
@Entity
@Table(name = "sucursales")
public class SucursalEntity extends EntidadAuditada {

    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private UUID id;

    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @NotNull(message = "La sucursal debe pertenecer a una franquicia")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "franquicia_id", nullable = false)
    private FranquiciaEntity franquicia;

    @OneToMany(mappedBy = "sucursal", fetch = FetchType.LAZY)
    private Set<ProductoEntity> productos = new LinkedHashSet<>();

    public SucursalEntity() {
        // Constructor vacio requerido por JPA.
    }

    public SucursalEntity(String nombre, FranquiciaEntity franquicia) {
        this.nombre = nombre;
        this.franquicia = franquicia;
    }

    /**
     * Prepara campos obligatorios para mantener consistencia antes de persistir.
     */
    @PrePersist
    @PreUpdate
    void prepararPersistencia() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }

        this.nombre = this.nombre == null ? null : this.nombre.trim();
        if (this.nombre == null || this.nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la sucursal es obligatorio");
        }

        if (this.franquicia == null) {
            throw new IllegalArgumentException("La sucursal debe pertenecer a una franquicia");
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

    public FranquiciaEntity getFranquicia() {
        return franquicia;
    }

    public void setFranquicia(FranquiciaEntity franquicia) {
        this.franquicia = franquicia;
    }

    public Set<ProductoEntity> getProductos() {
        return productos;
    }

    public void setProductos(Set<ProductoEntity> productos) {
        this.productos = productos;
    }
}
