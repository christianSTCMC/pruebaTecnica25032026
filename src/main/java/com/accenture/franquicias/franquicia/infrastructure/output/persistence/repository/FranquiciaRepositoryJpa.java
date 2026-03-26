package com.accenture.franquicias.franquicia.infrastructure.output.persistence.repository;

import com.accenture.franquicias.franquicia.infrastructure.output.persistence.entity.FranquiciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de acceso a datos para operaciones CRUD basicas de franquicias.
 */
public interface FranquiciaRepositoryJpa extends JpaRepository<FranquiciaEntity, UUID> {

    /**
     * Retorna franquicias ordenadas por nombre para respuestas deterministas.
     */
    List<FranquiciaEntity> findAllByOrderByNombreAsc();

    /**
     * Permite validar duplicidad de nombre de franquicia en creacion.
     */
    boolean existsByNombre(String nombre);

    /**
     * Permite validar duplicidad de nombre excluyendo la franquicia actual.
     */
    boolean existsByNombreAndIdNot(String nombre, UUID franquiciaId);
}
