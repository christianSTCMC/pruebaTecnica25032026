package com.accenture.franquicias.franquicia.infrastructure.output.persistence.repository;

import com.accenture.franquicias.franquicia.infrastructure.output.persistence.entity.FranquiciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Puerto de acceso a datos para operaciones CRUD basicas de franquicias.
 */
public interface FranquiciaRepositoryJpa extends JpaRepository<FranquiciaEntity, UUID> {
}
