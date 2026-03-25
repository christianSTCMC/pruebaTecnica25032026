-- Esquema inicial para Fase 1: modelo de dominio y persistencia.
-- Se implementan restricciones para garantizar consistencia del negocio.

CREATE TABLE franquicias (
    id CHAR(36) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_franquicias PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE sucursales (
    id CHAR(36) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    franquicia_id CHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_sucursales PRIMARY KEY (id),
    CONSTRAINT fk_sucursales_franquicias FOREIGN KEY (franquicia_id) REFERENCES franquicias (id)
) ENGINE=InnoDB;

CREATE TABLE productos (
    id CHAR(36) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    stock INT NOT NULL,
    sucursal_id CHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT pk_productos PRIMARY KEY (id),
    CONSTRAINT fk_productos_sucursales FOREIGN KEY (sucursal_id) REFERENCES sucursales (id),
    CONSTRAINT ck_productos_stock_no_negativo CHECK (stock >= 0),
    CONSTRAINT uk_productos_sucursal_nombre UNIQUE (sucursal_id, nombre)
) ENGINE=InnoDB;

-- Indices para mejorar busquedas por relacion y consulta de mayor stock.
CREATE INDEX idx_sucursales_franquicia_id ON sucursales (franquicia_id);
CREATE INDEX idx_productos_sucursal_stock_nombre ON productos (sucursal_id, stock DESC, nombre ASC);
