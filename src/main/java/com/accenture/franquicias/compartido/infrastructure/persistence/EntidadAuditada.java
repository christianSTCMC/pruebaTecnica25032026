package com.accenture.franquicias.compartido.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;

/**
 * Define metadatos de auditoria comunes para todas las entidades persistentes.
 *
 * <p>Se usa {@link Instant} para mantener consistencia en UTC y evitar
 * ambiguedades entre zonas horarias al persistir y consultar datos.</p>
 */
@MappedSuperclass
public abstract class EntidadAuditada {

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Inicializa timestamps al crear un registro nuevo.
     */
    @PrePersist
    protected void alCrear() {
        Instant ahora = Instant.now();
        this.createdAt = ahora;
        this.updatedAt = ahora;
    }

    /**
     * Actualiza el timestamp de modificacion en cada cambio persistido.
     */
    @PreUpdate
    protected void alActualizar() {
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
