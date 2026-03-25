package com.accenture.franquicias.sucursal.infrastructure.output.persistence.repository;

import com.accenture.franquicias.sucursal.infrastructure.output.persistence.entity.SucursalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio con operaciones de consulta para sucursales por franquicia.
 */
public interface SucursalRepositoryJpa extends JpaRepository<SucursalEntity, UUID> {

    /**
     * Retorna sucursales ordenadas por nombre para exponer respuestas deterministas.
     */
    List<SucursalEntity> findByFranquicia_IdOrderByNombreAsc(UUID franquiciaId);
}
