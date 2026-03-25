package com.accenture.franquicias.franquicia.infrastructure.output.persistence.entity;

import com.accenture.franquicias.compartido.infrastructure.persistence.EntidadAuditada;
import com.accenture.franquicias.sucursal.infrastructure.output.persistence.entity.SucursalEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad persistente que representa una franquicia en base de datos.
 *
 * <p>Se modela como raiz del agregado para permitir relacionar multiples
 * sucursales bajo un mismo identificador de negocio.</p>
 */
@Entity
@Table(name = "franquicias")
public class FranquiciaEntity extends EntidadAuditada {

    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private UUID id;

    @NotBlank(message = "El nombre de la franquicia es obligatorio")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @OneToMany(mappedBy = "franquicia", fetch = FetchType.LAZY)
    private Set<SucursalEntity> sucursales = new LinkedHashSet<>();

    public FranquiciaEntity() {
        // Constructor vacio requerido por JPA.
    }

    public FranquiciaEntity(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Normaliza y valida datos obligatorios antes de insertar o actualizar.
     */
    @PrePersist
    @PreUpdate
    void prepararPersistencia() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }

        this.nombre = this.nombre == null ? null : this.nombre.trim();
        if (this.nombre == null || this.nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la franquicia es obligatorio");
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

    public Set<SucursalEntity> getSucursales() {
        return sucursales;
    }

    public void setSucursales(Set<SucursalEntity> sucursales) {
        this.sucursales = sucursales;
    }
}
