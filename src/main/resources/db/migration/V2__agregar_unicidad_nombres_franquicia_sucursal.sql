-- Agrega restricciones de unicidad para reforzar reglas de negocio.
-- Se evita permitir nombres duplicados de franquicia y sucursal.

ALTER TABLE franquicias
    ADD CONSTRAINT uk_franquicias_nombre UNIQUE (nombre);

ALTER TABLE sucursales
    ADD CONSTRAINT uk_sucursales_nombre UNIQUE (nombre);
