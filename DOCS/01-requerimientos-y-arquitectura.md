# Requerimientos y arquitectura

## Alcance funcional documentado

El sistema modela una jerarquía de negocio:

- Una `franquicia` contiene múltiples `sucursales`.
- Una `sucursal` contiene múltiples `productos`.
- Un `producto` tiene `nombre` y `stock`.

## Trazabilidad de criterios de aceptación

| Criterio | Estado actual | Evidencia técnica |
| --- | --- | --- |
| API en Spring Boot | Implementado | `pom.xml` con `spring-boot-starter-parent:3.5.12` |
| Agregar franquicia | Implementado | `POST /api/v1/franquicias` |
| Actualizar nombre de franquicia | Implementado | `PATCH /api/v1/franquicias/{franquiciaId}/nombre` |
| Agregar sucursal a franquicia | Implementado | `POST /api/v1/franquicias/{franquiciaId}/sucursales` |
| Actualizar nombre de sucursal | Implementado | `PATCH /api/v1/sucursales/{sucursalId}/nombre` |
| Agregar producto a sucursal | Implementado | `POST /api/v1/sucursales/{sucursalId}/productos` |
| Actualizar nombre de producto | Implementado | `PATCH /api/v1/productos/{productoId}/nombre` |
| Eliminar producto de sucursal | Implementado | `DELETE /api/v1/sucursales/{sucursalId}/productos/{productoId}` |
| Modificar stock | Implementado | `PATCH /api/v1/productos/{productoId}/stock` |
| Consultar mayor stock por sucursal | Implementado | `GET /api/v1/franquicias/{franquiciaId}/productos/mayor-stock-por-sucursal` |
| Consultar mayor stock por sucursal (listado plano) | Implementado | `GET /api/v1/franquicias/{franquiciaId}/productos/mayor-stock-por-sucursal/listado` |
| Persistencia en sistema tipo MySQL/Redis/Mongo/DynamoDB | Implementado para MySQL | `Spring Data JPA` + `MySQL` + migración Flyway |

## Arquitectura técnica implementada

### Estilo por capas y contexto

La base del proyecto sigue organización por contexto funcional y capas:

- `franquicia`, `sucursal`, `producto`: contexto de negocio.
- `compartido`: componentes transversales.

Cada contexto separa:

- `domain`: núcleo conceptual.
- `application`: casos de uso y DTOs de aplicación.
- `infrastructure/entrypoint/rest`: adaptadores HTTP.
- `infrastructure/output/persistence`: entidades y repositorios JPA.

### Patrón reactivo + persistencia bloqueante controlada

- Capa HTTP implementada con `WebFlux` y `Mono<ResponseEntity<...>>`.
- Persistencia JPA encapsulada en `Schedulers.boundedElastic()` para evitar bloqueo del event loop.
- Resultado: contrato reactivo en entrada, con aislamiento explícito del acceso bloqueante a base de datos.

## Modelo de datos

El esquema relacional se crea con `V1__crear_esquema_inicial_franquicias.sql`.

### Entidades principales

- `franquicias`
  - `id` (`CHAR(36)`) PK
  - `nombre` (`VARCHAR(150)`) NOT NULL
- `sucursales`
  - `id` (`CHAR(36)`) PK
  - `nombre` (`VARCHAR(150)`) NOT NULL
  - `franquicia_id` FK -> `franquicias.id`
- `productos`
  - `id` (`CHAR(36)`) PK
  - `nombre` (`VARCHAR(150)`) NOT NULL
  - `stock` (`INT`) NOT NULL
  - `sucursal_id` FK -> `sucursales.id`

### Restricciones de consistencia

- `CHECK (stock >= 0)` en `productos`.
- `UNIQUE (sucursal_id, nombre)` para evitar duplicados por sucursal.
- Índice `idx_productos_sucursal_stock_nombre` para consulta de mayor stock por sucursal.

## Reglas de negocio implementadas

- `nombre` obligatorio para franquicia, sucursal y producto.
- `stock` obligatorio y no negativo.
- No se permite duplicidad de nombre de franquicia.
- No se permite duplicidad de nombre de sucursal.
- Una sucursal debe pertenecer a una franquicia existente.
- Un producto debe pertenecer a una sucursal existente.
- No se permite duplicidad de nombre de producto en una misma sucursal.
- La actualizacion de nombre de producto conserva la unicidad por sucursal.
- En empate por stock máximo, gana el nombre alfabéticamente menor.
- En la consulta agregada, solo aparecen sucursales con productos.

## Componentes transversales

### Errores homogéneos

`GlobalExceptionHandler` centraliza respuestas con `ApiErrorResponse`:

- `400` para validación y entrada inválida.
- `404` para recursos inexistentes.
- `409` para conflictos de negocio.
- `500` para errores no controlados.

### Observabilidad

- `RequestLoggingFilter` registra método, ruta, estado y latencia.
- Se propaga o genera `X-Request-Id` por petición.
- `ApplicationLifecycleLogger` registra hitos de arranque y parada.

### OpenAPI

- Configuración central en `OpenApiConfig`.
- Exposición del contrato con `springdoc` bajo rutas estándar del proyecto.

## Estado de diseño frente a operación cloud

- La base técnica de persistencia está implementada sobre MySQL.
- La estrategia de despliegue cloud está definida a nivel documental, no operacionalizada en este repositorio.
